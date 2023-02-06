(ns boss.core
  (:require
    [compojure.core :refer :all]
    [environ.core :refer [env]]
    [ring.adapter.jetty :refer [run-jetty]]))

(defroutes app
  (GET "/" [] "Hello world"))

(def port- (Integer/parseInt (env :port)))

(defn -main
  [& args]
  (run-jetty app {:port port-}))
