(ns parser
  (:require [schema :as s]
            [xml :as xml]
            [clojure.string :as str]))

(defn parse-node-with-schema [node {:keys [collection? value? fields attrs] :as schema}]
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

(defn parse-node [{:keys [tag attrs content] :as tag-node}]
  (let [first-content (first content)]
    {tag (merge attrs
                (cond
                  (map? first-content)
                  (->> content
                       (remove (fn [{:keys [attrs content] :as nod}]
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

(defn ccda-xml->json [parsed-xml]
  (parse-node parsed-xml))

(defn ccda-file->json [file-path]
  (comment (ccda-file->json "resource/sample.xml"))

  (let [doc (xml/parse-xml-file file-path)]
    (ccda-xml->json doc)
    #_(xml/parse-xml-file file-path)))

