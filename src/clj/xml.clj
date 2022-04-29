(ns xml
  (:require [clojure.xml :as xml])
  (:import (java.io File FileInputStream BufferedInputStream)))

(defn parse-xml-file [^String file-path]
  (with-open [fr (FileInputStream. (File. file-path))
              br (BufferedInputStream. fr)]
    (xml/parse br)))

(defn get-child-nodes [node tag]
  (->> (:content node)
       (filterv #(= tag (:tag %)))))
