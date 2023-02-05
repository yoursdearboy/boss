(ns boss.util
  (:require
    [next.jdbc.plan :as plan]
    [honey.sql :as sql]))

(defn select-max [ds query column]
  (plan/select-one! ds :max (sql/format {:select [[[:max column] :max]] :from query})))

(defn column-name [def]
  (if (vector? def) (second def) def))

(defn column-names [defs]
  (map column-name defs))

(defn namespace-keyword [x] (keyword (namespace x)))

(defn group-by-unique [f coll]
  (zipmap (map f coll) coll))
