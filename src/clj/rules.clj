(ns rules)

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

;; (->> mapping-rules
;;      vals
;;      (mapv #(maprule cda-cash %)))

;; orient on microsoft:
;; template -> fhir


;; take section
;; take template id
;; write rule engine (sic!)
;; proto FHIR resource

;(def FHIR-Bundle
;  {:ProgressNote
;   {:problem [:section :problem :conclusion]
;    :allergies [:section :allergies :foo]
;    :document {:resourceType "DocumentReference"
;               :id [:resource :documentReference]}}
;   :Patient []})


(def mapping-rules
  {:->location
   {:template ["2.16.840.1.113883.10.20.22.4.32"]
    :paths [{:rim {:path [:addr 0 :streetAddressLine]}
             :fhir {:path [:Location :address :* :line]
                    :inner {:path [:root]}}}
            {:rim {:path [:addr 0 :streetAddressLine]}
             :fhir {:path [:Location :address :* :line]
                    :inner {:path [:root]}}}]}

   :encoutner
   {:template [{:extension "2014-06-09",
                :root "2.16.840.1.113883.10.20.22.2.22.1"}]
    :paths [{:rim {:path [:entry :encounter :participant :participantRole
                          {:templateId :->location}]}
             :fhir {:path [:Encounter :location]}}

            {:rim {:path [:id 0]}
             :fhir {:path [:Encounter :identifier]
                    :inner {:path [:root]}}}
            {:rim {:path [:id 0 :extension]}
             :fhir {:path [[:Encounter :identifier :* :system]
                           [:Encounter :identifier :* :use]]}}]}})





(defn map-using-rules [cda-cache]
  {:resourceType "Bundle"
   :entry []})