(ns boss.frontend.components)

(defn button [options & text]
  [:button (merge-with into options {:class ["btn" "btn-light" "btn-outline-dark"]}) text])

(defn card [{:keys [title body body-after]}]
  [:div.card.mb-2
   [:div.card-body
    [:h5.card-title title]
    body]
   body-after])

(defn delete-button [handler]
  (button {:class ["btn-close"]
           :on-click handler}))

(defn modal-button [options id text]
  (button (merge options {:data-bs-toggle "modal"
                          :data-bs-target (str "#" id)})
          text))

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

(defn table [rows]
  [:table.table
   [:tbody
    (for [[i row] (map-indexed vector rows)]
      ^{:key i}
      [:tr
       (for [[j col] (seq row)]
         ^{:key j}
         [:td col])])]])
