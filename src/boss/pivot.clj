(ns boss.pivot
  (:require
    [boss.util :refer [column-names group-by-unique namespace-keyword select-max]]))

(defn pivot-index [query by]
  {:select [:* [[:over [[:row_number] {:partition-by [by]}]] :pivot-index]]
   :from query})

(defn pivot-max [ds query by]
  (select-max ds (pivot-index query by) :pivot-index))

(defn pivot-column [c i]
  [[:filter [:max (-> c name keyword)] {:where [:= :pivot-index [:raw i]]}]
   (keyword (format "%s_%s" (name c) i))])

(defn pivot [ds query by columns]
  (let [indexed (pivot-index query by)
        imax (pivot-max ds query by)
        bundles (for [i (range imax) c columns] (pivot-column c (+ i 1)))
        columns (concat [by] bundles)]
    {:select columns :from indexed :group-by [by]}))

; FIXME: Move query processing in separate
(defn pivot-clause? [clause]
  (= :pivot (get-in clause [0 0])))

(defn pivot-clause [[[_ from by] alias]]
  {:from from :by by :ns (or alias from)})

(defn pivot-query-part [ds froms query]
  (let [columns (->> query :select (group-by namespace-keyword))
        pivots (->> query froms (filter pivot-clause?) (map pivot-clause) (group-by-unique :ns))
        pivots (update-vals pivots (fn [{:keys [from by ns]}] (pivot ds from by (get columns ns))))]
    (-> query
        (assoc :select (map (fn [[key default]]
                              (if (contains? pivots key)
                                (->> pivots key :select column-names rest) default))
                            columns))
        (update :select (partial apply concat))
        (update froms (partial map (fn [default]
                                     (if (pivot-clause? default)
                                       (let [ns (-> default pivot-clause :ns)]
                                         [(ns pivots) ns])
                                       default)))))))

(defn pivot-query [ds query]
  (cond->> query
    true (pivot-query-part ds :from)
    (contains? query :join) (pivot-query-part ds :join)
    (contains? query :left-join) (pivot-query-part ds :left-join)
    (contains? query :right-join) (pivot-query-part ds :right-join)
    (contains? query :inner-join) (pivot-query-part ds :inner-join)
    (contains? query :outer-join) (pivot-query-part ds :outer-join)
    (contains? query :full-join) (pivot-query-part ds :full-join)))
