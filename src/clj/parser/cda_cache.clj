(ns parser.cda-cache
  (:require [util.lang :as l]))

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


;; Example of result cache:
{{:root "encounter-tmpl-id" :extension "2014-06-09"}
 {"uri:123123-12-312312-41240" {:parent nil
                                :raw {:templateId [{:root "encounter-tmpl-id",}]
                                      :desctiption "section 1 data"}}
  "uri:1" {:templateId [{:root "encounter-tmpl-id",}]
           :desctiption "section 2 data"}}

 {:root "location-tmpl-id" :extension "2014-06-09"}
 {"uri:0" {:parent "uri:encounter-0" ;; uri:observation-0
           :parent-path [:entry :encounter :participant :participantRole]
           :raw {:templateId [{:root "observation-tmpl-id"}]
                 :desctiption "cholera"}}
  "uri:1" {:parentTemplateId 2 ;; uri:observation-1
           :raw {:templateId [{:root "observation-tmpl-id",}]
                 :desctiption "cough"}}
  "uri:2" {:templateId [{:root "observation-tmpl-id"}]  ;; uri:observation-2
           :desctiption "temperature"}
  "uri:3" {:templateId [{:root "observation-tmpl-id"}]
           :desctiption "cholera"}
  "uri:4" {:templateId [{:root "observation-tmpl-id"}]
           :desctiption "blood pressure"}}}

;; 1
{:path [:entry :encounter :participant :participantRole]}

(defn form-cda-cache-from-section [cda-raw-resource parent-id]
  (let [section-by-tmpl-id
        (->> cda-raw-resource
             (group-by #(get-most-specific-template-id (:templateId %)))
             (l/v-map (fn [sections-of-tmpl-id]
                        (->> sections-of-tmpl-id
                             (reduce (fn [acc section]
                                       (let [resource-id (str (random-uuid))
                                             #_#_inner-cache (form-cda-cache-from-body
                                                              section resource-id)]
                                         (assoc acc
                                           resource-id
                                           {:raw section
                                            :parent parent-id})))
                                     {})))))]
    section-by-tmpl-id))

;; TODO: set parent id to each resource
(defn form-cda-cache-from-body [cda-edn parent-id]
  (let [section-by-tmpl-id
        (->> (l/get-path cda-edn [:ClinicalDocument :component 0 :structuredBody :* :component])
             (flatten)
             (mapcat :section)
             (map form-cda-cache-from-section))]
    section-by-tmpl-id))

(defn form-cda-cache [cda-edn]
  (merge
   (form-cda-cache-from-body cda-edn nil)))
