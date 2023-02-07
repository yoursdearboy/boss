(ns ^:figwheel-hooks boss.frontend.app
  (:require [ajax.core :refer [GET POST]]
            [clojure.edn :as edn]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

;; Query atom and machinery
(defonce query (r/atom {:select []
                        :from []}))

(defn update-query! [in f]
  (swap! query #(update % in f)))

(defn add-from-to-query [table]
  (update-query! :from #(conj % (keyword table))))

(defn remove-from-from-query [table]
  (update-query! :from #(into [] (remove #{table} %))))

(defn add-select-to-query [column]
  (update-query! :select #(conj % (keyword column))))

(defn remove-select-from-query [column]
  (update-query! :select #(into [] (remove #{column} %))))

;; Supportive state
(defonce columns (r/atom []))
(defonce tables (r/atom []))
(defonce data (r/atom []))

(defn fetch-tables []
  (GET "/tables"
    {:handler (comp #(reset! tables %) edn/read-string)}))

(defn fetch-columns []
  (POST "/query/columns"
    {:body (pr-str (assoc @query :select [:*]))
     :handler (comp #(reset! columns %) edn/read-string)
     :error-handler println}))

(defn re-fetch-columns []
  (reset! columns [])
  (fetch-columns))

(defn fetch-data []
  (POST "/query"
    {:body (pr-str @query)
     :handler (comp #(reset! data %) edn/read-string)}))

;; Misc UI
(defn card [title body after]
  [:div.card.mb-2
   [:div.card-body
    [:h5.card-title title]
    body]
   after])

(defn delete-button [handler]
  [:button.btn-close {:on-click handler}])

(defn modal-button [options id text]
  [:button (merge options {:type "button" :data-bs-toggle "modal" :data-bs-target (str "#" id)}) text])

(defn modal [{:keys [id title body-before body-after]} & body]
  [:div.modal {:id id}
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header
      [:h5.modal-title title]
      [:button.btn-close {:data-bs-dismiss "modal"}]]
     body-before
     (cond (some? body) [:div.modal-body body])
     body-after]]])

;; UI
(defn from-list-element [x]
  ^{:key x}
  [:button {:class [:list-group-item :list-group-item-action]
            :on-click #(add-from-to-query x)} x])

(defn from-list []
  (card "Add some data" nil
        [:div.list-group.list-group-flush
         (map from-list-element @tables)]))

(defn query-tree-select-list-element [column]
  ^{:key column}
  [:div.list-group-item
   [:span column]
   [:span.float-end (delete-button #(remove-select-from-query column))]])

(defn query-tree-select-list []
  (card "Some columns"
        (modal-button {:class ["btn btn-light btn-outline-dark"]
                       :on-click re-fetch-columns}
                      "select-modal"
                      "Add")
        [:div.list-group.list-group-flush
         (map query-tree-select-list-element (:select @query))]))

(defn query-tree-from-list-element [i x]
  ^{:key i}
  [:div.list-group-item
   [:span x]
   [:span.float-end (delete-button #(remove-from-from-query x))]])

(defn query-tree-select-modal-element [i column]
  ^{:key i}
  [:button {:class [:list-group-item :list-group-item-action]
            :on-click #(add-select-to-query column)} column])

(defn query-tree-select-modal []
  (modal {:id "select-modal"
          :title "Select columns"
          :body-after [:div.list-group
                       (map-indexed query-tree-select-modal-element @columns)]}))

(defn query-tree-from-list []
  (card "Some tables" nil
        [:div.list-group.list-group-flush
         (map-indexed query-tree-from-list-element (:from @query))]))

(defn query-tree []
  [:div
   (query-tree-select-modal)
   (query-tree-select-list)
   (query-tree-from-list)])

(defn data-table [rows]
  [:table.table
   [:tbody
    (for [[i row] (map-indexed vector rows)]
      ^{:key i}
      [:tr
       (for [[j col] (seq row)]
         ^{:key j}
         [:td col])])]])

(defn data-ui []
  [:div
   [:button.btn.btn-default.btn-outline-dark {:on-click fetch-data} "Fetch data"]
   (data-table @data)])

(defn layout [main right]
  [:div.container
   [:div.row
    [:div.col-sm-9 main]
    [:div.col-sm-3 right]]])

(defn app []
  (layout [:div
           (data-ui)]
          [:div
           (query-tree)
           (from-list)]))

(defn mount []
  (fetch-tables)
  (rdom/render [app] (js/document.getElementById "app")))

(mount)
