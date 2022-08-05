(ns parser
  (:require [schema :as s]
            [xml :as xml]
            [clojure.string :as str]
            [clojure.walk :as w]))

(def d
  (atom {}))

(defn debug
  [k v]
  (swap! d assoc (gensym k) v))

(defn next [state acc [tag content]]
  (let [state (or state :init)
        acc   (or acc {})
        _ (debug :state  state)
        _ (debug :content  content)]
    (case state
      :exit acc

      :init
      (case tag
        :ClinicalDocument
        (next :read-tree acc [tag content])

        (next :exit acc))

      :read-tree
      (cond
        (and (nil? tag) (nil? content))
        (next :exit acc [nil nil])

        (= :patient tag)
        (next :read-tree
              (->> content
                   (filter #(= :name (key %)))
                   (reduce-kv (fn [a _ v]
                                (assoc-in a :name
                                          (mapv
                                           (fn [x]
                                             {:family (get-in x [:family 0 :value 0])
                                              :given  (mapv #(get-in % [:value 0]) (:given x))})
                                           v)))
                              acc)))
       (not (map? content)) (next :read-tree acc [nil nil])

        true
        (reduce-kv (fn [a k v]
                     (next :read-tree a [k v]))
                   acc
                   content)))))


(defn parse-node-with-schema
  [node {:keys [collection? value? fields attrs] :as schema}]
  #_(prn "NOD " (:tag node) schema (when-not (= :ClinicalDocument (:tag node))
                                     (:content node)))
  (cond
    (:ccda.entity schema)
    (parse-node-with-schema node (get s/entities-schema (:ccda.entity schema)))

    value?
    (let [content (:content node)]
      (if (and (sequential? content)
               (not collection?))
        (first content)
        content))

    :else
    (let [attrs (->> attrs
                     (map (fn [[attr-key schema]]
                            (let [value (get-in node [:attrs attr-key])
                                  error? (and (:required? schema)
                                              (nil? value))]
                              [attr-key (if error?
                                          {:error "Attribute is required"
                                           :attr attr-key
                                           :id (:id schema)}
                                          (get-in node [:attrs attr-key]))]))))
          fields (->> fields
                      (map (fn [[field-key schema]]
                             (let [nodes (->> (xml/get-child-nodes node field-key)
                                              (mapv #(parse-node-with-schema % schema)))
                                   missing? (and (:required? schema)
                                                 (empty? nodes))
                                   nested-errors (->> nodes
                                                      (filter #(not-empty (:errors %)))
                                                      (mapcat :errors)
                                                      (not-empty))]
                               [field-key (cond
                                            missing? {:error "Field is required"
                                                      :tag field-key
                                                      :id (:id schema)}
                                            nested-errors {:error "Field contains nested errors"
                                                           :tag field-key
                                                           :id (:id schema)
                                                           :errors nested-errors}
                                            (:collection? schema) nodes
                                            :else (first nodes))]))))
          missing-values (->> (concat attrs fields)
                              (filter #(some? (get (second %) :error)))
                              (mapv second))]
      (if (not-empty missing-values)
        {:errors missing-values}
        (->> (concat attrs fields)
             (into {}))))))

;(defn ccda-xml->json [parsed-xml]
;  (let [doc-schemas (->> (xml/get-child-nodes parsed-xml :templateId)
;                         (map :attrs)
;                         (map (fn [{:keys [root extension]}]
;                                (str root (when extension ":") extension)))
;                         (map #(get s/doc-schema %))
;                         (filter some?))
;        parsed (->> doc-schemas
;                    (map #(parse-node-with-schema parsed-xml %))
;                    (apply merge))]
;    parsed))

(defn parse-node
  [{:keys [tag attrs content] :as tag-node}]
  (let [first-content (first content)]
    {tag (merge attrs
                (cond
                  (map? first-content)
                  (->> content
                       (remove (fn [{:keys [attrs content]}]
                                 (and (nil? attrs)
                                      (nil? content))))
                       (mapv parse-node)
                       (reduce (fn [m node]
                                 (let [[tag node-value] (first node)]
                                   (update m tag #(-> (or % [])
                                                      (conj node-value)))))
                               {}))
                  (string? first-content)
                  {:value content}

                  (nil? content) nil

                  :else {:ERROR content}))}))

(defn ccda-xml->json
  [parsed-xml]
  (parse-node parsed-xml))

(defn ccda-file->json
  [file-path]
  (comment (ccda-file->json "resource/sample.xml"))
  (comment (xml/parse-xml-file "resource/sample.xml"))

  (let [doc (xml/parse-xml-file file-path)]
    (ccda-xml->json doc)
    #_(xml/parse-xml-file file-path)))


(w/postwalk (fn [x])
           (ccda-file->json "resource/sample.xml"))

(defn get-path [m [k & ks :as keys]]
  (if k
    (if (= :* k)
      (let [res (mapv #(get-path % ks) m)]
        (if (empty? res) nil res))

      (get-path
       (cond
         (map? k) (some (fn [i] (and (= i (into i k)) i)) m)
         (number? k) (if (and (sequential? m) (< k (count m))) (nth m k) nil)
         :else (get m k))
       ks))
    m))

(-> "resource/sample.xml"
    ccda-file->json
    (get-path [:ClinicalDocument :component 0
               :structuredBody
               :* :component])
    flatten
    (->> (filterv (fn [x] (->> x
                               :section
                               (mapv :templateId)
                               (mapv :root))))))

    ;; all entities that have :component keyword
    ;; and from them - filter one section
    ;; that has templateId root value:
    ;; 1.3.6.1.4.1.19376.1.5.3.1.3.1

(def cda-cash {"1.3.6.1.4.1.19376.1.5.3.1.3.1"
               [{:templateId
                 [{:extension "2014-06-09", :root "1.3.6.1.4.1.19376.1.5.3.1.3.1"}
                  {:root "1.3.6.1.4.1.19376.1.5.3.1.3.1"}],
                 :code [{:codeSystem "2.16.840.1.113883.6.1", :code "42349-1"}],
                 :title [{:value ["Reason for Referral"]}],
                 :text
                 [{:list
                   [{:item
                     [{:paragraph
                       [{:styleCode "xSecondary",
                         :value
                         ["Ms Alice Newman is being referred to Community Health Hospitals Inpatient facility because of the high fever noticed and suspected Anemia."]}]}]}]}]}]})

(defn get-most-specific-template-id
  "TODO: Choose template id with the newest extension"
  [template-ids]
  (first template-ids))

(defn form-cda-cache-from-body [cda-edn]
  (->> (get-path cda-edn [:ClinicalDocument :component 0
                          :structuredBody
                          :* :component])
       (flatten)
       (mapcat :section)
       (group-by #(get-most-specific-template-id (:templateId %)))))

(defn form-cda-cache [cda-edn]
  (comment (form-cda-cache (ccda-file->json "resource/encounter-example.xml")))
  (merge
   (form-cda-cache-from-body cda-edn)))

;; (declare defrule)

;; (defrule "1.3.6.1.4.1.19376.1.5.3.1.3.1"
;;   (fn [bullshit] (get-in bullshit [:title 0 :value])))

;; (defn maprule
;;   [cda-cash {:keys [template path]
;;         :as rule}]
;;   (mapv (fn [] (get-in ))))

;; (defmulti dispatch-template
;;   (fn [x] (->> x :section first :templateId (mapv :root))))

;; (defmethod)

(def mapping-rules
  {:discharge-summary {:template ["1.3.6.1.4.1.19376.1.5.3.1.3.1:2014-06-09"
                                  "1.3.6.1.4.1.19376.1.5.3.1.3.1"]
                       :paths {[:title 0 :value]
                               [[:DocumentReference :subject]]}}})

;; (->> mapping-rules
;;      vals
;;      (mapv #(maprule cda-cash %)))

;; orient on microsoft:
;; template -> fhir


;; take section
;; take template id
;; write rule engine (sic!)
;; proto FHIR resource

(def FHIR-Bundle
  {:ProgressNote
   {:problem [:section :problem :conclusion]
    :allergies [:section :allergies :foo]
    :document {:resourceType "DocumentReference"
               :id [:resource :documentReference]}}
   :Patient []})
