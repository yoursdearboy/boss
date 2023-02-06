(ns boss.frontend.app
  (:require [reagent.dom :as rdom]))

(defn demo []
  [:div
   [:h1 "Hello World"]])

(defn ^:export run []
  (rdom/render [demo] (js/document.getElementById "app")))
