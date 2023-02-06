(ns boss.core
  (:require
    [compojure.core :refer :all]
    [ring.adapter.jetty :refer [run-jetty]]))

(defroutes app
  (GET "/" [] "Hello world"))

(defn -main
  [& args]
  (run-jetty app {:port 3000}))
