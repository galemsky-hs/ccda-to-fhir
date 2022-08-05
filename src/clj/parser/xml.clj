(ns parser.xml
  (:require [xml :as xml]))

(defn- parse-node
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

(defn ccda-xml->edn
  [parsed-xml]
  (parse-node parsed-xml))

(defn ccda-file->edn
  [file-path]
  (comment (ccda-file->edn "resource/sample.xml"))
  (comment (ccda-file->edn "resource/encounter-example.xml"))
  (comment (xml/parse-xml-file "resource/sample.xml"))

  (let [doc (xml/parse-xml-file file-path)]
    (ccda-xml->edn doc)
    #_(xml/parse-xml-file file-path)))
