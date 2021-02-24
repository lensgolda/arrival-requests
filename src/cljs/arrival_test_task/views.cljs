(ns arrival-test-task.views
  (:require
    [re-frame.core :as re-frame]
    [arrival-test-task.subs :as subs]
    [arrival-test-task.events :as events]
    [clojure.string :as str]))


(defonce ^:private fields
  [:header :description :applicant :performer])

(defn table-header []
  [:tr
   [:th "id"]
   [:th "header"]
   [:th "description"]
   [:th "applicant"]
   [:th "performer"]
   [:th "date"]])

(defn table-row
  [{:keys [temp-id header description applicant performer date]}]
  [:tr.row
   [:td temp-id]
   [:td header]
   [:td description]
   [:td applicant]
   [:td performer]
   [:td date]])


(defn requests-list []
  (let [_ (re-frame/dispatch [::events/get-requests-list])
        requests-list (re-frame/subscribe [::subs/requests-list])
        loading? (re-frame/subscribe [::subs/loading?])]
    (fn []
      (if @loading?
        [:div "Loading requests list..."]
        [:table {:width "100%"
                 :style {:border-collapse :collapse}
                 :border 2
                 :cellPadding 7}
         [:thead {:align "left"} [table-header]]
         [:tbody {:align "left"}
          (doall
            (for [r @requests-list]
              ^{:key r} [table-row r]))]]))))


(defn request-form []
  (let [data (re-frame/subscribe [::subs/form-data])]
    (fn []
      [:div#form
       (doall
         (for [field fields]
           ^{:key field}
           [:div {:style {:padding "20px 0px"}}
            [:div {:style {:width "200px" :text-align :right :float :left}}
             [:div [:label {:for field} (str/capitalize (name field))]]]
            [:div {:style {:text-align :left :margin-left "250px"}}
             [:div [:input {:type :text
                            :name field
                            :size 50
                            :on-change (fn [e]
                                         (re-frame/dispatch
                                           [::events/set-input-value
                                            (keyword field)
                                            (-> e .-target .-value)]))}]]]]))
       [:div {:style {:padding "20px 0px"}}
        [:div {:style {:width "200px" :text-align :right :float :left}}
         [:button.btn {:on-click #(re-frame/dispatch [::events/create-request @data])
                       :style {:margin-left "20px"}}
          "Submit"]]]])))

(defn app-view []
  (let [view-type (re-frame/subscribe [::subs/app-view])
        ready? (re-frame/subscribe [::subs/initialized?])]
    (fn []
      [:div
       [:div#buttons
        [:button.btn {:on-click (fn [e]
                                  (.preventDefault e)
                                  (re-frame/dispatch [::events/switch-type :list]))}
         "List requests"]
        [:button.btn {:style {:margin-left "20px"}
                      :on-click (fn [e]
                                  (.preventDefault e)
                                  (re-frame/dispatch [::events/switch-type :form]))}
         "Create request"]]
       [:div#requests {:style {:padding-top "50px"}}
        (case @view-type
          :list (if-not ready?
                  [:div "Initializing..."]
                  [requests-list])
          :form [request-form]
          [:h3 "Ooops...Something went wrong..."])]])))