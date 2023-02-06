(ns boss.core
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defroutes app
  (route/resources "/"))

(def port- (Integer/parseInt (env :port)))

(defn -main
  [& _]
  (run-jetty app {:port port-}))
