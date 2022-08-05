(ns parser.patient
  (:require [parser :as p]))

(p/next nil nil [:ClinicalDocument (:ClinicalDocument (p/ccda-file->json "resource/sample.xml"))])

; @p/d

(defn parse-patient
  [acc {:keys [patient]}]
  {})

(defmulti dispatch-resource
  (fn [acc resource-key node-content]
    tag))

(defmethod dispatch-resource :patient
  [acc {:keys [content] :as tag}]
  #_(parse-patient acc tag))

(let [name-vec [{:use "L",
                 :family [{:value ["Newman"]}],
                 :given [{:value ["Alice"]} {:value ["Jones"]}]}
                {:family [{:nullFlavor "NI"}],
                 :given [{:qualifier "BR", :value ["Alicia"]}]}]]
(->> name-vec
     (filterv (comp #{:name} :tag))))
