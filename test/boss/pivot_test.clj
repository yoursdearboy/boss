(ns boss.pivot-test
  (:require [next.jdbc :as jdbc]
            [boss.pivot :refer [pivot]]
            [clojure.test :refer :all]))

(def db {:dbtype "sqlite" :dbname "test/demo.db"})
(def ds (jdbc/get-datasource db))

(deftest test-pivot
  (is (= (pivot ds :diagnoses [:icd_code] :patient_id)
         ["SELECT patient_id, MAX(icd_code) FILTER (WHERE pivot_index = 1), MAX(icd_code) FILTER (WHERE pivot_index = 2) FROM (SELECT *, (ROW_NUMBER() OVER (PARTITION BY patient_id)) AS pivot_index FROM diagnoses) GROUP BY patient_id"])))
