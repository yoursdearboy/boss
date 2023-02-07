(ns ^:figwheel-hooks boss.frontend.app
  (:require [ajax.core :refer [GET POST]]
            [boss.frontend.components :refer [button card delete-button modal
                                              modal-button table]]
            [clojure.edn :as edn]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

;; Query atom and machinery
(defonce query (r/atom {:select []
                        :from []}))

(defn update-query! [in f]
  (swap! query #(update % in f)))

(defn add-from-to-query! [table]
  (update-query! :from #(conj % (keyword table))))

(defn remove-from-from-query! [table]
  (update-query! :from #(into [] (remove #{table} %))))

(defn add-select-to-query! [column]
  (update-query! :select #(conj % (keyword column))))

(defn remove-select-from-query! [column]
  (update-query! :select #(into [] (remove #{column} %))))

;; Supportive state
(defonce columns (r/atom []))
(defonce tables (r/atom []))
(defonce data (r/atom []))

(defn fetch-tables! []
  (GET "/tables"
    {:handler (comp #(reset! tables %) edn/read-string)}))

(defn re-fetch-tables! []
  (reset! tables [])
  (fetch-tables!))

(defn fetch-columns! []
  (POST "/query/columns"
    {:body (pr-str (assoc @query :select [:*]))
     :handler (comp #(reset! columns %) edn/read-string)
     :error-handler println}))

(defn re-fetch-columns! []
  (reset! columns [])
  (fetch-columns!))

(defn fetch-data! []
  (POST "/query"
    {:body (pr-str @query)
     :handler (comp #(reset! data %) edn/read-string)}))

;; UI
(defn from-list-element [x]
  ^{:key x}
  [:button {:class [:list-group-item :list-group-item-action]
            :on-click #(add-from-to-query! x)} x])

(defn from-list []
  [:div.list-group
   (map from-list-element @tables)])

(defn query-tree-select-list-element [column]
  ^{:key column}
  [:div.list-group-item
   [:span column]
   [:span.float-end (delete-button #(remove-select-from-query! column))]])

(defn query-select-list []
  (card {:title "Some columns"
         :body (modal-button {:on-click re-fetch-columns!}
                             "select-modal"
                             "Add")
         :body-after [:div.list-group.list-group-flush
                      (map query-tree-select-list-element (:select @query))]}))

(defn query-tree-from-list-element [i x]
  ^{:key i}
  [:div.list-group-item
   [:span x]
   [:span.float-end (delete-button #(remove-from-from-query! x))]])

(defn query-tree-select-modal-element [i column]
  ^{:key i}
  [:button {:class [:list-group-item :list-group-item-action]
            :on-click #(add-select-to-query! column)} column])

(defn query-select-modal []
  (modal {:id "select-modal"
          :title "Select columns"
          :body-after [:div.list-group
                       (map-indexed query-tree-select-modal-element @columns)]}))

(defn query-from-list []
  (card {:title "Some tables"
         :body (modal-button {:on-click re-fetch-tables!} "from-modal" "Add")
         :body-after [:div.list-group.list-group-flush
                      (map-indexed query-tree-from-list-element (:from @query))]}))

(defn query-from-modal []
  (modal {:id "from-modal"
          :title "Add from"
          :body-after (from-list)}))

(defn query-ui []
  [:div
   (query-select-modal)
   (query-select-list)
   (query-from-modal)
   (query-from-list)])

(defn data-ui []
  [:div
   (button {:on-click fetch-data!} "Fetch data")
   (table @data)])

(defn layout [main right]
  [:div.container
   [:div.row
    [:div.col-sm-9 main]
    [:div.col-sm-3 right]]])

(defn app []
  (layout
   (data-ui)
   (query-ui)))

(defn mount []
  (rdom/render [app] (js/document.getElementById "app")))

(mount)
