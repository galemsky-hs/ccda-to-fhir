(ns parser.patient)

(defn parse-patient
  [acc {:keys [patient]}])

(cond-> {}
  patient (parse-patient patient))


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
     (filterv (comp #{:name} :tag))
     ))
