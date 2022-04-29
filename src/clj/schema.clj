(ns schema)

;; Samples
;; https://github.com/chb/sample_ccdas
;; https://github.com/HL7/C-CDA-Examples

;; HL7 CDA R2 Implementation Guide: Consolidated CDA Templates for Clinical Notes
;; http://www.hl7.org/implement/standards/product_brief.cfm?product_id=492

(def entities-schema
  {:ccda.entities/id
   {:id "CONF:1198-5363"
    :attrs {:root {:required? true}
            :extension {}
            :nullFlavor {}}
    :fields []}

   :ccda.entities/addr
   {:id "2.16.840.1.113883.10.20.22.5.2"
    :attrs {:use {}}
    :fields {:country {:value? true}
             ;:testMissing {:required? true :value? true}
             :state {:value? true}
             :city {:required? true
                    :value? true}
             :postalCode {:value? true}
             :streetAddressLine {:value? true}}}})


(def doc-schema
  {"2.16.840.1.113883.10.20.22.1.1"
   {:name :ccda.docs/us-realm-header
    :fields {:id {:required? true
                  :ccda.entity :ccda.entities/id}
             :recordTarget {:collection? true
                            :required? true
                            :fields
                            {:patientRole {:required? true
                                           :fields
                                           {:id {:required? true
                                                 :attrs {:root {:required? true}
                                                         :extension {}}}
                                            :addr {:collection? true
                                                   :required? true
                                                   :ccda.entity :ccda.entities/addr}
                                            :telecom {:collection? true
                                                      :required? true}
                                            :patient {:required? true
                                                      :fields
                                                      {:name {:collection? true
                                                              :required? true}}}}}}}}}})
