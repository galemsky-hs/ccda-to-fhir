(ns parser-old
  (:require [schema :as s]
            [xml :as xml]
            [clojure.string :as str]
            [clojure.walk :as w]))

(def *debug
  (atom {}))

(defn debug
  [k v]
  (swap! *debug assoc (gensym k) v))

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

