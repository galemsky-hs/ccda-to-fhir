(ns parser-test
  (:require  [clojure.test :refer :all]
             [matcho.core :as matcho]
             [parser :as p]))

(deftest automate-programming-test
  (def name
    {:tag :patient
     :attrs nil
     :content [{:tag :name,
                :attrs {:use "L"},
                :content
                [{:tag :family, :attrs nil, :content ["Newman"]}
                 {:tag :given, :attrs nil, :content ["Alice"]}
                 {:tag :given, :attrs nil, :content ["Jones"]}]}]})
  (testing "name "
    (matcho/match (p/parse-patient name {:practitioner {}})
                  {:patient {:name [{:given ["Alice" "Jones"]
                                     :family "Newman"}]}
                   :practitioner {}})))

(deftest parser-initial-test
  (testing "if something unusual is met - we add ERROR"
    (def first-map {:tag :route
                    :attrs {}
                    :content [{:tag :error-nested
                               :attrs {:error :error-content}
                               :content []}
                              {:tag :nested2
                               :attrs {:moo :moo}
                               :content [{:tag :nested-nested,
                                          :attrs {:extension "2015-08-01",
                                                  :root "2.16.840.1.113883.10.20.22.1.14"},
                                          :content nil}]}
                              {:tag :templateId,
                               :attrs {:extension "2015-08-01",
                                       :root "2.16.840.1.113883.10.20.22.1.14"},
                               :content nil}
                              {:tag :nested3
                               :attrs {:boo :boo}
                               :content ["megastring"]}]})
    (matcho/match (p/parse-node first-map)
                  {:route
                   {:error-nested [{:error :error-content, :ERROR []}],
                    :nested2
                    [{:moo :moo,
                      :nested-nested
                      [{:extension "2015-08-01",
                        :root "2.16.840.1.113883.10.20.22.1.14"}]}],
                    :templateId
                    [{:extension "2015-08-01", :root "2.16.840.1.113883.10.20.22.1.14"}],
                    :nested3 [{:boo :boo, :value ["megastring"]}]}})))
