(ns parser.cda-cache
  (:require [util.lang :as l]))

;; Example of result cache:
{{:extension "2014-06-09", :root "2.16.840.1.113883.10.20.22.2.22.1"}
 {"uri:4ceb45ae-c756-4219-9c79-fb337f327fae" {:resource-id "uri:4ceb45ae-c756-4219-9c79-fb337f327fae",
                                              :raw {,,,},
                                              ;; Encounters section
                                              :templateId {:extension "2014-06-09",
                                                           :root "2.16.840.1.113883.10.20.22.2.22.1"},
                                              :parent nil,
                                              :parent-path []}},
 {:extension "2014-06-09", :root "2.16.840.1.113883.10.20.22.4.49"}
 {"uri:5887af70-7f6d-4bf5-b3e6-5b3dbbd48143" {:resource-id "uri:5887af70-7f6d-4bf5-b3e6-5b3dbbd48143",
                                              :raw {,,,},
                                              ;; Encounter
                                              :templateId {:extension "2014-06-09",
                                                           :root "2.16.840.1.113883.10.20.22.4.49"},
                                              :parent "uri:7b441469-6262-4a74-a4dc-bb234327d5df",
                                              :parent-path [:entry
                                                            :encounter]}},
 {:extension "2014-06-09", :root "2.16.840.1.113883.10.20.22.4.19"}
 {"uri:90126383-bdf3-4181-a65e-fc9788d8c060" {:resource-id "uri:90126383-bdf3-4181-a65e-fc9788d8c060",
                                              :raw {,,,},
                                              ;; Observation
                                              :templateId {:extension "2014-06-09",
                                                           :root "2.16.840.1.113883.10.20.22.4.19"},
                                              :parent "uri:bbb85315-cb80-4a56-ba1d-6b51a260c16a",
                                              :parent-path [:entry
                                                            :encounter
                                                            :entryRelationship
                                                            :observation]}},
 {:root "2.16.840.1.113883.10.20.22.4.32"}
 {"uri:7a0d045d-b4ee-42f5-a76f-66ac4f4b3a36" {:resource-id "uri:7a0d045d-b4ee-42f5-a76f-66ac4f4b3a36",
                                              :raw {,,,}
                                              ;; Location
                                              :templateId {:root "2.16.840.1.113883.10.20.22.4.32"},
                                              :parent "uri:18d7a583-123c-4473-873e-9f9125e634e7",
                                              :parent-path [:entry
                                                            :encounter
                                                            :participant
                                                            :participantRole]}}}

(defn random-fhir-uri []
  (str "uri:" (random-uuid)))

(defn get-most-specific-template-id
  "TODO: Choose template id with the newest extension"
  [resource]
  (first (:templateId resource)))

(declare sections->cda-cache)

(defn section->cda-cache [section resource-id parent-id parent-path]
  (cond
    (string? section)
    {}

    (map? section)
    {:resource-id resource-id
     :raw section
     :templateId (get-most-specific-template-id section)
     :parent parent-id
     :parent-path parent-path
     :cache (->> section
                 (reduce-kv
                  (fn [acc field-key value]
                    (l/deep-merge
                     acc (sections->cda-cache value resource-id (conj parent-path field-key))))
                  {}))}

    :else (prn "WTF?" section)))

(defn sections->cda-cache [sections parent-id parent-path]
  (if-not (sequential? sections)
    {}
    (->> sections
         (map #(section->cda-cache
                % (random-fhir-uri) parent-id parent-path))
         (map (fn [{:keys [resource-id raw templateId cache] :as section-data}]
                (if templateId
                  (merge-with concat
                              {templateId {resource-id (dissoc section-data :cache)}}
                              cache)
                  cache)))
         (apply merge-with concat))))

(defn body->cda-cache [cda-edn]
  (comment (body->cda-cache (parser.xml/ccda-file->edn "resource/encounter-example.xml")))

  (->> (l/get-path cda-edn [:ClinicalDocument :component 0 :structuredBody :* :component])
       (flatten)
       (mapcat :section)
       (#(sections->cda-cache % nil []))))


(defn form-cda-cache [cda-edn]
  (merge
   (body->cda-cache cda-edn)))
