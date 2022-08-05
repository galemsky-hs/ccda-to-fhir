(ns util.lang)

(defn get-path [m [k & ks :as keys]]
  (if k
    (if (= :* k)
      (let [res (mapv #(get-path % ks) m)]
        (if (empty? res) nil res))

      (get-path
       (cond
         (map? k) (some (fn [i] (and (= i (into i k)) i)) m)
         (number? k) (if (and (sequential? m) (< k (count m))) (nth m k) nil)
         :else (get m k))
       ks))
    m))

(defn v-map
  "Performs function `f` on the values of the map `m`.
  Supports passing function arguments mostly to use it for maps which values are collections."
  [& args]
  ;; Examples:
  (comment (v-map inc {:a 1})
           (v-map filterv some? {:a [1 2 nil]})
           (v-map v-map mapv :count {:a {:a1 [{:count 1 :role "RN"}
                                              {:count 2 :role "LPN"}]
                                         :a2 [{:count 10 :role "RN"}]}}))
  (let [m (last args)
        fn-on-val (first args)
        fn-args (rest (butlast args))]
    (reduce-kv (fn [m k vals]
                 (assoc m k (apply fn-on-val (concat fn-args [vals]))))
               {}
               m)))

