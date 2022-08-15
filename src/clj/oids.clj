(ns oids
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [xml :as xml]))

(def oids
  {"2.16.840.1.113883.10.20.22.1.4" :consultation-note-v3
   "2.16.840.1.113883.10.20.22.4.133" :wound-measurement-observation
   "2.16.840.1.113883.10.20.22.4.38" :social-history-observation
   "2.16.840.1.113883.10.20.22.4.123" :drug-monitoring-act
   "2.16.840.1.113883.10.20.22.1.3" :history-and-physical-v3
   "2.16.840.1.113883.10.20.22.4.36" :admission-medication
   "2.16.840.1.113883.10.20.15.3.8" :pregnancy-observation
   "2.16.840.1.113883.10.20.6.2.14" :quantity-measurement-observation
   "2.16.840.1.113883.10.20.22.2.20" :past-medical-history
   "2.16.840.1.113883.10.20.22.2.21"
   :advance-directives-section-entries-optional-v3
   "2.16.840.1.113883.10.20.6.2.9" :purpose-of-reference-observation
   "2.16.840.1.113883.10.20.21.2.2" :subjective-section
   "2.16.840.1.113883.10.20.22.2.41"
   :hospital-discharge-instructions-section
   "2.16.840.1.113883.10.20.22.2.34" :preoperative-diagnosis-section-v3
   "2.16.840.1.113883.10.20.22.1.14" :referralnote
   "2.16.840.1.113883.10.20.6.2.10" :referenced-frames-observation
   "2.16.840.1.113883.10.20.22.4.17" :medication-supply-order
   "2.16.840.1.113883.10.20.22.4.16" :medication-activity
   "2.16.840.1.113883.10.20.1.19" :authorization-activity
   "2.16.840.1.113883.10.20.22.5.2" :usrealmaddressadusfielded
   "2.16.840.1.113883.10.20.22.4.135" :medical-equipment-organizer
   "2.16.840.1.113883.10.20.22.1.6" :procedure-note
   "2.16.840.1.113883.10.20.22.4.114"
   :longitudinal-care-wound-observation
   "2.16.840.1.113883.10.20.22.4.66" :functional-status-organizer
   "2.16.840.1.113883.10.20.22.4.143" :prioritypreference
   "2.16.840.1.113883.10.20.22.4.47" :family-history-death-observation
   "2.16.840.1.113883.10.20.22.4.145" :criticality-observation
   "2.16.840.1.113883.10.20.22.4.63" :series-act
   "2.16.840.1.113883.10.20.22.4.121" :goalobservation
   "2.16.840.1.113883.10.20.22.2.1"
   :medications-section-entries-optional
   "2.16.840.1.113883.10.20.22.4.74" :mental-status-observation
   "2.16.840.1.113883.10.20.22.4.2" :result-observation
   "2.16.840.1.113883.10.20.22.4.68"
   :functional-status-problem-observation-deprecated
   "2.16.840.1.113883.10.20.22.2.22"
   :encounters-section-entries-optional
   "2.16.840.1.113883.10.20.21.2.3" :interventions-section
   "2.16.840.1.113883.10.20.22.2.31" :procedure-specimens-taken-section
   "2.16.840.1.113883.10.20.22.4.33" :hospital-discharge-diagnosis
   "2.16.840.1.113883.10.20.22.2.15" :family-history-section
   "2.16.840.1.113883.10.20.24.3.90"
   :substance-or-device-allergy---intolerance-observation
   "2.16.840.1.113883.10.20.22.4.51" :postprocedure-diagnosis-v3
   "2.16.840.1.113883.10.20.22.2.2.1"
   :immunizations-section-entries-required
   "2.16.840.1.113883.10.20.22.1.10" :unstructureddocument
   "2.16.840.1.113883.10.20.22.1.2" :continuityofcaredocumentccd
   "2.16.840.1.113883.10.20.6.2.5" :procedure-context
   "2.16.840.1.113883.10.20.22.1.15" :care-plan-v2
   "2.16.840.1.113883.10.20.22.4.140" :patient-referral-act
   "2.16.840.1.113883.10.20.22.2.28" :procedure-findings-section-v3
   "2.16.840.1.113883.10.20.22.2.22.1"
   :encounters-section-entries-required
   "2.16.840.1.113883.10.20.29.1"
   :us-realm-header-for-patient-generated-document-v2
   "2.16.840.1.113883.10.20.22.4.119" :authorparticipation
   "2.16.840.1.113883.10.20.22.2.39" :medical-general-history-section
   "2.16.840.1.113883.10.20.7.13" :surgical-drains-section
   "2.16.840.1.113883.10.20.22.4.70"
   :pressure-ulcer-observation-deprecated
   "2.16.840.1.113883.10.20.22.4.1" :result-organizer
   "2.16.840.1.113883.10.20.22.2.1.1"
   :medications-section-entries-required
   "2.16.840.1.113883.10.20.22.4.118" :substance-administered-act
   "2.16.840.1.113883.10.20.22.2.29" :procedure-indications-section-v2
   "2.16.840.1.113883.10.20.22.4.120" :planned-immunization-activity
   "2.16.840.1.113883.10.20.22.4.122" :entry-reference
   "2.16.840.1.113883.10.20.22.2.42" :hospital-consultations-section
   "2.16.840.1.113883.10.20.22.4.34" :hospital-admission-diagnosis
   "2.16.840.1.113883.10.20.22.4.41" :planned-procedure
   "1.3.6.1.4.1.19376.1.5.3.1.3.26" :hospital-discharge-physical-section
   "2.16.840.1.113883.10.20.15.3.1" :estimated-date-of-delivery
   "2.16.840.1.113883.10.20.22.4.48" :advance-directive-observation-v3
   "1.3.6.1.4.1.19376.1.5.3.1.3.5" :hospital-course-section
   "2.16.840.1.113883.10.20.22.4.144" :outcome-observation
   "2.16.840.1.113883.10.20.22.4.115" :external-document-reference
   "2.16.840.1.113883.10.20.21.2.1" :objective-section
   "2.16.840.1.113883.10.20.22.2.5" :problem-section-entries-optional-v3
   "2.16.840.1.113883.10.20.22.4.20" :instruction
   "1.3.6.1.4.1.19376.1.5.3.1.3.18" :review-of-systems-section
   "2.16.840.1.113883.10.20.22.4.60" :coverage-activity
   "2.16.840.1.113883.10.20.22.4.124" :nutritional-status-observation
   "2.16.840.1.113883.10.20.22.4.39" :planned-act
   "2.16.840.1.113883.10.20.22.2.58" :health-concerns-section-v2
   "2.16.840.1.113883.10.20.22.5.3"
   :us-realm-date-and-time-dt.us.fielded
   "2.16.840.1.113883.10.20.22.4.8" :severity-observation
   "2.16.840.1.113883.10.20.22.2.4.1"
   :vital-signs-section-entries-required
   "2.16.840.1.113883.10.20.22.4.53" :immunization-refusal-reason
   "2.16.840.1.113883.10.20.22.4.19" :indication
   "2.16.840.1.113883.10.20.22.4.61" :policy-activity
   "1.3.6.1.4.1.19376.1.5.3.1.3.1" :reason-for-referral-section
   "2.16.840.1.113883.10.20.22.2.18" :payers-section
   "2.16.840.1.113883.10.20.22.2.60" :goals-section
   "2.16.840.1.113883.10.20.22.4.67" :functional-status-observation
   "2.16.840.1.113883.10.20.22.2.2"
   :immunizations-section-entries-optional
   "2.16.840.1.113883.10.20.22.2.7" :procedures-section-entries-optional
   "2.16.840.1.113883.10.20.22.4.113" :prognosisobservation
   "1.3.6.1.4.1.19376.1.5.3.1.3.33" :discharge-diet-section-deprecated
   "2.16.840.1.113883.10.20.22.5.4" :usrealmdateandtimedtmusfielded
   "2.16.840.1.113883.10.20.22.2.38"
   :medications-administered-section-v2
   "2.16.840.1.113883.10.20.22.4.3" :problemconcernact
   "2.16.840.1.113883.10.20.22.4.108" :advance-directive-organizer-v2
   "2.16.840.1.113883.10.20.22.2.43" :admission-diagnosis-section
   "2.16.840.1.113883.10.20.22.4.109"
   :characteristics-of-home-environment
   "2.16.840.1.113883.10.20.22.2.3" :results-section-entries-optional
   "2.16.840.1.113883.10.20.22.4.27" :vital-sign-observation
   "2.16.840.1.113883.10.20.7.12" :operative-note-fluids-section
   "2.16.840.1.113883.10.20.22.4.45" :family-history-organizer
   "2.16.840.1.113883.10.20.22.2.14" :functional-status-section
   "2.16.840.1.113883.10.20.22.4.31" :ageobservation
   "2.16.840.1.113883.10.20.22.2.40" :procedure-implants-section
   "2.16.840.1.113883.10.20.22.4.46" :family-history-observation
   "2.16.840.1.113883.10.20.22.1.7" :operative-note-v3
   "2.16.840.1.113883.10.20.22.4.146" :planned-intervention-act
   "2.16.840.1.113883.10.20.22.2.4"
   :vital-signs-section-entries-optional
   "2.16.840.1.113883.10.20.22.2.9" :assessment-and-plan-section
   "2.16.840.1.113883.10.20.22.2.23" :medical-equipment-section
   "2.16.840.1.113883.10.20.22.2.33" :implants-section-deprecated
   "2.16.840.1.113883.10.20.22.2.24" :discharge-diagnosis-section
   "2.16.840.1.113883.10.20.22.2.6"
   :allergiesandintolerancessectionentriesoptional
   "2.16.840.1.113883.10.20.22.2.6.1"
   :allergiesandintolerancessectionentriesrequired
   "2.16.840.1.113883.10.20.22.2.7.1"
   :procedures-section-entries-required
   "2.16.840.1.113883.10.20.22.4.138" :nutrition-assessment
   "2.16.840.1.113883.10.20.22.4.134" :wound-characteristic
   "2.16.840.1.113883.10.20.18.2.9"
   :procedure-estimated-blood-loss-section
   "2.16.840.1.113883.10.20.22.4.72" :caregiver-characteristics
   "2.16.840.1.113883.10.20.22.4.52" :immunization-activity
   "2.16.840.1.113883.10.20.22.4.50" :non-medicinal-supply-activity
   "2.16.840.1.113883.10.20.22.4.35" :discharge-medication-v3
   "2.16.840.1.113883.10.20.6.2.2" :physician-of-record-participant-v2
   "2.16.840.1.113883.10.20.22.4.129" :plannedcoverage
   "1.3.6.1.4.1.19376.1.5.3.1.1.13.2.1" :chief-complaint-section
   "2.16.840.1.113883.10.20.6.1.1"
   :dicom-object-catalog-section---dcm-121181
   "2.16.840.1.113883.10.20.22.2.11.1"
   :discharge-medications-section-entries-required-v3
   "2.16.840.1.113883.10.20.22.4.23" :medication-information
   "2.16.840.1.113883.10.20.22.2.5.1"
   :problem-section-entries-required-v3
   "2.16.840.1.113883.10.20.22.2.64" :course-of-care-section
   "2.16.840.1.113883.10.20.22.4.111"
   :cultural-and-religious-observation
   "2.16.840.1.113883.10.20.22.4.73"
   :cognitive-status-problem-observation-deprecated
   "1.3.6.1.4.1.19376.1.5.3.1.3.4" :history-of-present-illness-section
   "2.16.840.1.113883.10.20.22.2.16"
   :hospital-discharge-studies-summary-section
   "2.16.840.1.113883.10.20.6.2.4" :observer-context
   "2.16.840.1.113883.10.20.22.4.110" :progress-toward-goal-observation
   "2.16.840.1.113883.10.20.22.4.132" :health-concern-act-v2
   "2.16.840.1.113883.10.20.22.4.7" :allergy---intolerance-observation
   "2.16.840.1.113883.10.20.22.2.45" :instructions-section-v2
   "2.16.840.1.113883.10.20.22.4.78" :smoking-status---meaningful-use
   "2.16.840.1.113883.10.20.22.4.77" :highest-pressure-ulcer-stage
   "2.16.840.1.113883.10.20.22.2.61"
   :health-status-evaluations-and-outcomes-section
   "2.16.840.1.113883.10.20.22.4.69" :assessment-scale-observation
   "2.16.840.1.113883.10.20.22.4.37" :product-instance
   "2.16.840.1.113883.10.20.22.4.26" :vital-signs-organizer
   "2.16.840.1.113883.10.20.22.2.17" :social-history-section
   "2.16.840.1.113883.10.20.22.4.85" :tobacco-use
   "2.16.840.1.113883.10.20.22.2.37" :complications-section-v3
   "2.16.840.1.113883.10.20.22.2.30" :planned-procedure-section-v2
   "2.16.840.1.113883.10.20.22.1.8" :discharge-summary-v3
   "2.16.840.1.113883.10.20.22.4.136" :risk-concern-act-v2
   "2.16.840.1.113883.10.20.22.5.1.1" :usrealmpersonnamepnusfielded
   "2.16.840.1.113883.10.20.6.2.11" :boundary-observation
   "2.16.840.1.113883.10.20.2.5" :general-status-section
   "2.16.840.1.113883.10.20.22.4.43" :planned-supply
   "2.16.840.1.113883.10.20.6.2.12" :text-observation
   "2.16.840.1.113883.10.20.22.4.128" :self-care-activities-adl-and-iadl
   "2.16.840.1.113883.10.20.6.2.6" :study-act
   "2.16.840.1.113883.10.20.22.2.25" :anesthesia-section
   "2.16.840.1.113883.10.20.22.4.131" :intervention-act
   "2.16.840.1.113883.10.20.6.1.2" :findings-section-dir
   "2.16.840.1.113883.10.20.22.2.27" :procedure-description-section
   "2.16.840.1.113883.10.20.22.4.79" :deceased-observation-v3
   "2.16.840.1.113883.10.20.22.4.5" :health-status-observation-v2
   "2.16.840.1.113883.10.20.6.2.3" :fetus-subject-context
   "2.16.840.1.113883.10.20.22.4.54"
   :immunization-medication-information
   "2.16.840.1.113883.10.20.6.2.13" :code-observations
   "2.16.840.1.113883.10.20.22.4.30" :allergy-concern-act
   "2.16.840.1.113883.10.20.22.4.130" :nutrition-recommendation
   "2.16.840.1.113883.10.20.22.4.9" :reaction-observation
   "2.16.840.1.113883.10.20.6.2.1" :physician-reading-study-performer-v2
   "2.16.840.1.113883.10.20.22.4.32" :service-delivery-location
   "2.16.840.1.113883.10.20.22.4.49" :encounter-activity
   "2.16.840.1.113883.10.20.22.4.28" :allergy-status-observation
   "2.16.840.1.113883.10.20.22.2.3.1" :results-section-entries-required
   "2.16.840.1.113883.10.20.22.4.65" :preoperative-diagnosis-v3
   "2.16.840.1.113883.10.20.22.2.35" :postoperative-diagnosis-section
   "2.16.840.1.113883.10.20.22.2.10" :planoftreatmentsection
   "2.16.840.1.113883.10.20.22.2.21.1"
   :advance-directives-section-entries-required
   "2.16.840.1.113883.10.20.22.2.8" :assessment-section
   "2.16.840.1.113883.10.20.22.4.75" :mental-status-organizer
   "2.16.840.1.113883.10.20.22.4.64" :comment-activity
   "2.16.840.1.113883.10.20.22.4.127" :sensory-status
   "2.16.840.1.113883.10.20.22.2.13"
   :chief-complaint-and-reason-for-visit-section
   "2.16.840.1.113883.10.20.6.2.8" :sop-instance-observation
   "2.16.840.1.113883.10.20.22.4.6" :problemstatus
   "2.16.840.1.113883.10.20.22.4.24" :drug-vehicle
   "2.16.840.1.113883.10.20.22.4.76"
   :number-of-pressure-ulcers-observation
   "2.16.840.1.113883.10.20.22.2.36" :postprocedure-diagnosis-section-v3
   "2.16.840.1.113883.10.20.22.4.147" :medication-free-text-sig
   "2.16.840.1.113883.10.20.22.2.26"
   :surgery-description-section-deprecated
   "2.16.840.1.113883.10.20.22.4.13" :procedure-activity-observation
   "2.16.840.1.113883.10.20.7.14"
   :operative-note-surgical-procedure-section
   "2.16.840.1.113883.10.20.22.2.44"
   :admission-medications-section-entries-optional
   "2.16.840.1.113883.10.20.22.1.13" :transfersummary
   "2.16.840.1.113883.10.20.22.1.1" :usrealmheader
   "2.16.840.1.113883.10.20.22.4.14" :procedure-activity-procedure
   "2.16.840.1.113883.10.20.22.2.56" :mental-status-section
   "2.16.840.1.113883.10.20.22.4.4" :problemobservation
   "2.16.840.1.113883.10.20.22.4.12" :procedure-activity-act
   "2.16.840.1.113883.10.20.2.10" :physical-exam-section
   "2.16.840.1.113883.10.20.22.4.40" :planned-encounter
   "2.16.840.1.113883.10.20.22.4.80" :encounter-diagnosis
   "2.16.840.1.113883.10.20.22.4.42" :planned-medication-activity
   "2.16.840.1.113883.10.20.22.1.5" :diagnostic-imaging-report-v3
   "2.16.840.1.113883.10.20.22.2.57" :nutrition-section
   "2.16.840.1.113883.10.20.22.4.18" :medication-dispense
   "2.16.840.1.113883.10.20.18.2.12" :procedure-disposition-section
   "2.16.840.1.113883.10.20.22.5.1" :usrealmpatientnameptnusfielded
   "2.16.840.1.113883.10.20.22.2.11"
   :discharge-medications-section-entries-optional-v3
   "2.16.840.1.113883.10.20.22.4.141"
   :handoff-communication-participants
   "2.16.840.1.113883.10.20.22.4.86"
   :assessment-scale-supporting-observation
   "2.16.840.1.113883.10.20.22.1.9" :progress-note-v3
   "2.16.840.1.113883.10.20.22.4.25"
   :precondition-for-substance-administration
   "2.16.840.1.113883.10.20.22.4.44" :plannedobservation
   "2.16.840.1.113883.10.20.22.2.12" :reason-for-visit-section})

(comment
  (def oids
    (reduce (fn [acc x]
              (if (= (:tag x) :resource)
                (let [ref (-> (get-in x [:content 0 :content 0 :attrs :value])
                              (str/split #"/" 2)
                              (last))
                      id (-> (get-in x [:content 0 :content 1 :attrs :value] "")
                             (str/replace #"[()]" "")
                             (str/trim)
                             (str/lower-case)
                             (str/replace #"\s+" "-")
                             (keyword))]
                  (if (not= "xml" ref) (assoc acc ref id) acc))

                acc))
            {}
            (get-in (xml/parse-xml-file "resource/oids.xml") [:content 13 :content])))

  (clojure.pprint/pprint oids)


  )
