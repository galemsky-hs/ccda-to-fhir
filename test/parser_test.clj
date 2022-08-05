(ns parser-test
  (:require  [clojure.test :refer :all]
             [matcho.core :as matcho]
             [parser :as p]))


(deftest parse-encounter-xml-sample-cda-to-cda-cache
  (testing "Parse cda to edn"
    (def cda-edn (p/ccda-file->json "resource/encounter-example.xml"))

    (testing "Form cda-cache from parsed edn"
      (def cda-cache
        (p/form-cda-cache cda-edn)))

    (testing "cda-cache should contain expected encounter data"
      (def expected
        {{:root "2.16.840.1.113883.10.20.22.2.22.1" :extension "2014-06-09"}
         [{:templateId [{:extension "2014-06-09",
                         :root "2.16.840.1.113883.10.20.22.2.22.1"}],
           :code [{:displayName "History of encounters",
                   :codeSystemName "LOINC",
                   :codeSystem "2.16.840.1.113883.6.1",
                   :code "46240-8"}],
           :title [{:value ["ENCOUNTERS"]}],
           :text [{:table [{:width "100%",
                            :border "1",
                            :thead [{:tr [{:th [{:value ["Encounter"]}
                                                {:value ["Performer"]}
                                                {:value ["Location"]}
                                                {:value ["Date"]}]}]}],
                            :tbody [{:tr [{:td [{:ID "Encounter1",
                                                 :value [" Checkup Examination "]}
                                                {:value ["Performer Name"]}
                                                {:value ["Community Urgent Care Center"]}
                                                {:value ["September 27, 2012 at 1:00pm"]}]}]}]}]}],
           :entry [{:typeCode "DRIV",
                    :encounter [{:entryRelationship [{:typeCode "RSON",
                                                      :observation [{:moodCode "EVN",
                                                                     :classCode "OBS",
                                                                     :templateId [{:extension "2014-06-09",
                                                                                   :root "2.16.840.1.113883.10.20.22.4.19"}],
                                                                     :id [{:extension "45665",
                                                                           :root "db734647-fc99-424c-a864-7e3cda82e703"}],
                                                                     :code [{:displayName "Clinical finding",
                                                                             :codeSystemName "LOINC",
                                                                             :codeSystem "2.16.840.1.113883.6.1",
                                                                             :code "75321-0"}],
                                                                     :statusCode [{:code "completed"}],
                                                                     :effectiveTime [{:low [{:value "201209251130-0500"}]}],
                                                                     :value [{:codeSystem "2.16.840.1.113883.6.96",
                                                                              :displayName "Pneumonia",
                                                                              :code "233604007",
                                                                              :xsi:type "CD"}]}]}],
                                 :participant [{:typeCode "LOC",
                                                :participantRole [{:classCode "SDLOC",
                                                                   :templateId [{:root "2.16.840.1.113883.10.20.22.4.32"}],
                                                                   :code [{:displayName "Urgent Care Center",
                                                                           :codeSystemName "HL7 HealthcareServiceLocation",
                                                                           :codeSystem "2.16.840.1.113883.6.259",
                                                                           :code "1160-1"}],
                                                                   :addr [{:streetAddressLine [{:value ["1007 Health Drive"]}],
                                                                           :city [{:value ["Portland"]}],
                                                                           :state [{:value ["OR"]}],
                                                                           :postalCode [{:value ["99123"]}],
                                                                           :country [{:value ["US"]}]}],
                                                                   :telecom [{:value "tel: +1(555)555-1030",
                                                                              :use "WP"}],
                                                                   :playingEntity [{:classCode "PLC",
                                                                                    :name [{:value ["Good Health Urgent Care"]}]}]}]}],
                                 :classCode "ENC",
                                 :id [{:root "2a620155-9d11-439e-92b3-5d9815ff4de8"}],
                                 :code [{:codeSystem "2.16.840.1.113883.6.12",
                                         :codeSystemName "CPT-4",
                                         :displayName "Office outpatient visit 15 minutes",
                                         :code "99213",
                                         :originalText [{:reference [{:value "#Encounter1"}]}],
                                         :translation [{:codeSystemName "HL7 ActEncounterCode",
                                                        :displayName "Ambulatory",
                                                        :codeSystem "2.16.840.1.113883.5.4",
                                                        :code "AMB"}]}],
                                 :moodCode "EVN",
                                 :effectiveTime [{:value "201209271300-0500"}],
                                 :templateId [{:extension "2014-06-09",
                                               :root "2.16.840.1.113883.10.20.22.4.49"}],
                                 :performer [{:assignedEntity [{:id [{:root "2.16.840.1.113883.4.6",
                                                                      :extension "333444555"}],
                                                                :code [{:displayName "General Physician",
                                                                        :codeSystemName "SNOMED CT",
                                                                        :codeSystem "2.16.840.1.113883.6.96",
                                                                        :code "59058001"}]}]}]}]}]}]})

      (is (matcho/match cda-cache expected)))))


(deftest parse-encounter-xml-sample-cda-to-cda-cache
  )
