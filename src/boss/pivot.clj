(ns boss.pivot
  (:require
    [next.jdbc.plan :as plan]
    [honey.sql :as sql]
    [honey.sql.helpers :as h]))

(defn select-max [ds query column]
  (plan/select-one! ds :max (sql/format {:select [[[:max column] :max]] :from query})))

(defn pivot-index [query by]
  {:select [:* [(h/over [[:row_number] (h/partition-by by)]) :pivot-index]]
   :from query})

(defn pivot-max [ds query by]
  (select-max ds (pivot-index query by) :pivot-index))

(defn pivot-column [c i]
  [[:filter [:max c] {:where [:= :pivot-index [:raw i]]}]])

(defn pivot [ds query columns pivot-by]
  (let [indexed (pivot-index query pivot-by)
        imax (pivot-max ds query pivot-by)
        bundles (for [i (range imax) c columns] (pivot-column c (+ i 1)))
        columns (concat [pivot-by] bundles)]
    (sql/format {:select columns :from indexed :group-by [pivot-by]})))
