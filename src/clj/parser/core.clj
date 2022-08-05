(ns parser.core
  (:require [parser.xml :as xmlp]
            [parser.cda-cache :as c]
            [rules :as r]))

(defn ccda->fhir [file]
  (comment (ccda->fhir "resource/encounter-example.xml"))

  (let [edn (xmlp/ccda-file->edn file)
        ccda-cache (c/form-cda-cache edn)
        fhir (r/map-using-rules ccda-cache)]
    fhir
    ccda-cache))

