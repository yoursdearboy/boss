(ns boss.meta-test
  (:require
    [boss.db :refer :all]
    [boss.meta :refer :all]
    [boss.test :refer :all]
    [clojure.test :refer :all]))

(deftest test-list-tables
  (is (= (map :TABLE_NAME (list-tables ds))
         ["diagnoses" "donors" "donorships" "patients"])))

(deftest test-list-table-columns
  (is (= (map :COLUMN_NAME (list-table-columns ds "patients"))
         ["id" "last_name" "first_name" "middle_name" "dob" "sex"])))

(deftest test-list-query-columns
  (is (= (list-query-columns ds (load-sql "test/list-query-columns.sql"))
         (load-edn "test/list-query-columns-result.edn"))))
