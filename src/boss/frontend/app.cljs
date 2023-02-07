(ns ^:figwheel-hooks boss.frontend.app
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [ajax.core :refer [GET]]
            [clojure.edn :as edn]))

(defonce tables (r/atom []))

(defn fetch-tables []
  (GET "/tables"
    {:handler (comp #(reset! tables %) edn/read-string)}))

(defn from-list-element [x]
  ^{:key x}
  [:li (str "Table: " x)])

(defn from-list []
  [:div
   [:h1 "Select from"]
   [:ul (map from-list-element @tables)]])

(defn app []
  (fetch-tables)
  (from-list))

(defn mount []
  (rdom/render [app] (js/document.getElementById "app")))

(mount)
