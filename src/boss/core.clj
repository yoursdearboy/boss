(ns boss.core
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.response :as response]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :refer [run-jetty]]
            [boss.db :refer [ds]]
            [boss.meta :refer [list-tables list-table-columns]]))

(defn index [_]
  (response/file-response "index.html" {:root "resources/public"}))

(defn tables [_]
  (->> (list-tables ds)
       (map :TABLE_NAME)
       (pr-str)))

(defn table-columns [table]
  (->> (list-table-columns ds table)
       (map :COLUMN_NAME)
       (pr-str)))

(defroutes routes
  (GET "/" [] index)
  (GET "/tables" [] tables)
  (GET "/tables/:table" [table] (table-columns table))
  (route/resources "/"))

(def app
  (-> routes
      (wrap-reload)))

(defn -main
  [& _]
  (run-jetty app {:port (Integer. (env :port))}))
