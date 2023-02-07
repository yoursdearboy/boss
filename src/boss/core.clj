(ns boss.core
  (:require [boss.db :refer [ds]]
            [boss.meta :refer [list-query-columns
                               list-table-columns
                               list-tables]]
            [clojure.edn :as edn]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.util.response :as response]))

(defn index [_]
  (response/file-response "index.html" {:root "resources/public"}))

(defn tables []
  (->> (list-tables ds)
       (map :TABLE_NAME)
       (pr-str)))

(defn table-columns [table]
  (->> (list-table-columns ds table)
       (map :COLUMN_NAME)
       (pr-str)))

(defn query [request]
  (->> request :body
       (slurp)
       (edn/read-string)
       (sql/format)
       (jdbc/execute! ds)
       (pr-str)))

(defn query-columns [request]
  (->> request :body
       (slurp)
       (edn/read-string)
       (sql/format)
       (list-query-columns ds)
       (map :COLUMN_NAME)
       (pr-str)))

(defroutes routes
  (GET "/" [] index)
  (POST "/query" request (query request))
  (POST "/query/columns" request (query-columns request))
  (GET "/tables" [] (tables))
  (GET "/tables/:table" [table] (table-columns table))
  (route/resources "/"))

(def app
  (-> routes
      (wrap-webjars)
      (wrap-reload)))

(defn -main
  [& _]
  (run-jetty app {:port (Integer. (env :port))}))
