(ns boss.query-test
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [next.jdbc :as jdbc]
    [honey.sql :as sql]
    [clojure.test :refer :all]))

(def db {:dbtype "sqlite" :dbname "test/demo.db"})
(def ds (jdbc/get-datasource db))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))
    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(deftest test-select-from
  (let [query-path "test/select-from.edn"
        query-model (load-edn query-path)
        query (sql/format query-model)
        test (jdbc/execute! ds query)
        result [#:patients{:id 1 :last_name "Ivanov"}
                #:patients{:id 2 :last_name "Petrov"}
                #:patients{:id 3 :last_name "Svetova"}]]
    (is (= result test))))

(deftest test-select-from-join
  (let [query-path "test/select-from-join.edn"
        query-model (load-edn query-path)
        query (sql/format query-model)
        test (jdbc/execute! ds query)
        result [{:patients/id 1 :patients/last_name "Ivanov" :diagnoses/icd_code "D61"}
                {:patients/id 1 :patients/last_name "Ivanov" :diagnoses/icd_code "D61.9"}
                {:patients/id 2 :patients/last_name "Petrov" :diagnoses/icd_code "C92.0"}
                {:patients/id 3 :patients/last_name "Svetova" :diagnoses/icd_code "C92.0"}]]
    (is (= result test))))

(deftest test-select-from-join-nested
  (let [query-path "test/select-from-join-nested.edn"
        query-model (load-edn query-path)
        query (sql/format query-model)
        test (jdbc/execute! ds query)
        result [{:patients/id 1 :patients/last_name "Ivanov" :donorships/date "2000-01-01" :donors/last_name "Semenov"}
                {:patients/id 1 :patients/last_name "Ivanov" :donorships/date "2000-02-02" :donors/last_name "Livov"}
                {:patients/id 2 :patients/last_name "Petrov" :donorships/date "2000-03-03" :donors/last_name "Semenov"}
                {:patients/id 2 :patients/last_name "Petrov" :donorships/date "2000-04-04" :donors/last_name "Livov"}
                {:patients/id 3 :patients/last_name "Svetova" :donorships/date "2000-05-05" :donors/last_name "Livov"}]]
    (is (= result test))))
