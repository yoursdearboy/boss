(ns boss.query-test
  (:require
    [boss.db :refer :all]
    [boss.pivot :refer [pivot-query]]
    [boss.test :refer :all]
    [clojure.test :refer :all]
    [honey.sql :as sql]
    [next.jdbc :as jdbc]))

; FIXME: Test structures and/or queries and/or data?
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

(deftest test-select-from-pivot
  (let [query-path "test/select-from-pivot.edn"
        query (load-edn query-path)
        query (pivot-query ds query)
        query (sql/format query)
        test (jdbc/execute! ds query)
        result [{:icd_code_1 "D61", :icd_code_2 "D61.9"}
                {:icd_code_1 "C92.0", :icd_code_2 nil}
                {:icd_code_1 "C92.0", :icd_code_2 nil}]]
    (is (= test result))))

(deftest test-select-from-join-pivot
  (let [query-path "test/select-from-join-pivot.edn"
        query (load-edn query-path)
        query (pivot-query ds query)
        query (sql/format query)
        test (jdbc/execute! ds query)
        result [{:patients/last_name "Ivanov" :icd_code_1 "D61", :icd_code_2 "D61.9"}
                {:patients/last_name "Petrov" :icd_code_1 "C92.0", :icd_code_2 nil}
                {:patients/last_name "Svetova" :icd_code_1 "C92.0", :icd_code_2 nil}]]
    (is (= test result))))

(deftest test-select-from-join-nested-pivot
  (let [query-path "test/select-from-join-nested-pivot.edn"
        query (load-edn query-path)
        query (pivot-query ds query)
        query (sql/format query)
        test (jdbc/execute! ds query)
        result [{:donors/last_name "Semenov" :donorships/date "2000-01-01" :patients/last_name "Ivanov" :icd_code_1 "D61" :icd_code_2 "D61.9"}
                {:donors/last_name "Semenov" :donorships/date "2000-03-03" :patients/last_name "Petrov" :icd_code_1 "C92.0" :icd_code_2 nil}
                {:donors/last_name "Livov" :donorships/date "2000-02-02" :patients/last_name "Ivanov" :icd_code_1 "D61" :icd_code_2 "D61.9"}
                {:donors/last_name "Livov" :donorships/date "2000-04-04" :patients/last_name "Petrov" :icd_code_1 "C92.0" :icd_code_2 nil}
                {:donors/last_name "Livov" :donorships/date "2000-05-05" :icd_code_1 "C92.0" :icd_code_2 nil :patients/last_name "Svetova"}]]
    (is (= test result))))
