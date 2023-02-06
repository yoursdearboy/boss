(ns boss.pivot-test
  (:require
    [boss.db :refer :all]
    [boss.pivot :refer [pivot]]
    [clojure.test :refer :all]
    [honey.sql :as sql]))

(deftest test-pivot
  (is (= (sql/format (pivot ds :diagnoses :patient_id [:icd_code]))
         ["SELECT patient_id, MAX(icd_code) FILTER (WHERE pivot_index = 1) AS icd_code_1, MAX(icd_code) FILTER (WHERE pivot_index = 2) AS icd_code_2 FROM (SELECT *, ROW_NUMBER() OVER (PARTITION BY patient_id) AS pivot_index FROM diagnoses) GROUP BY patient_id"])))
