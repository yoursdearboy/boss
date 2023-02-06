(ns boss.meta
  (:require
    [next.jdbc :as jdbc]
    [next.jdbc.result-set :as rs]))

(defn query-metadata [ds query f]
  (reduce (fn [_ x] (f (rs/metadata x))) nil (jdbc/plan ds query)))

(defn list-tables [ds]
  (-> (.getMetaData (jdbc/get-connection ds))
      (.getTables nil nil nil (into-array ["TABLE" "VIEW"]))
      (rs/datafiable-result-set ds {:builder-fn rs/as-unqualified-maps})))

(defn list-table-columns [ds table]
  (-> (.getMetaData (jdbc/get-connection ds))
      (.getColumns nil nil table nil)
      (rs/datafiable-result-set ds {:builder-fn rs/as-unqualified-maps})))

(defn collect-query-columns- [md]
  (doall
    (map
      (fn [i]
        {
         :COLUMN_NAME (.getColumnName md i)
         :TABLE_NAME  (.getTableName md i)
         })
      (range 1 (+ 1 (.getColumnCount md))))))

(defn list-query-columns [ds query]
  (query-metadata ds query collect-query-columns-))
