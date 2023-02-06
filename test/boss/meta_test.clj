(ns boss.meta-test
  (:require
    [clojure.edn :as edn]
    [next.jdbc :as jdbc]
    [boss.meta :refer :all]
    [clojure.test :refer :all]))

; FIXME: Move database to config
(def db {:dbtype "sqlite" :dbname "test/demo.db"})
(def ds (jdbc/get-datasource db))

; FIXME: Move helpers to helpers
(defn load-sql [source]
  [(slurp source)])

(defn load-edn [source]
  "Load edn from file"
  (edn/read-string (slurp source)))

(deftest test-list-tables
  (is (= (map :TABLE_NAME (list-tables ds))
         ["diagnoses" "donors" "donorships" "patients"])))

(deftest test-list-table-columns
  (is (= (map :COLUMN_NAME (list-table-columns ds "patients"))
         ["id" "last_name" "first_name" "middle_name" "dob" "sex"])))

(deftest test-list-query-columns
  (is (= (list-query-columns ds (load-sql "test/list-query-columns.sql"))
         (load-edn "test/list-query-columns-result.edn"))))
