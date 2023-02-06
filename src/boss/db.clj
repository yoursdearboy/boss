(ns boss.db
  (:require
    [environ.core :refer [env]]
    [next.jdbc :as jdbc]))

(def ds (jdbc/get-datasource (env :database-url)))
