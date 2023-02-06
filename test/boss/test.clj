(ns boss.test
  (:require [clojure.edn :as edn]))

(defn load-sql [source]
  [(slurp source)])

(defn load-edn [source]
  (edn/read-string (slurp source)))
