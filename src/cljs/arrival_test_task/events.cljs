(ns arrival-test-task.events
  (:require
   [re-frame.core :as re-frame]
   [arrival-test-task.db :as db]
   [arrival-test-task.cofxs :as cofx]
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::switch-type
  (fn [db [_ type]]
    (assoc db :view-type type)))

(re-frame/reg-event-db
  ::process-response
  (fn [db [_ resp]]
    (assoc db :loading? false
              :requests (:items (js->clj resp))
              :view-type :list)))

(re-frame/reg-event-db
  ::bad-response
  (fn [db [_ resp]]
    (js/console.log (clj->js resp))
    (assoc db :loading? false)))

(re-frame/reg-event-fx
  ::get-requests-list
  (fn [{db :db} _]
    {:http-xhrio {:method :get
                  :uri "http://localhost:8080/list"
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::process-response]
                  :on-failure [::bad-response]}
     :db (assoc db :loading? true)}))

(re-frame/reg-event-fx
  ::create-request
  [(re-frame/inject-cofx ::cofx/now)
   (re-frame/inject-cofx ::cofx/temp-id)]
  (fn [{:keys [db temp-id now]} [_ form-data]]
    {:http-xhrio {:method :post
                  :uri "http://localhost:8080/list"
                  :params (assoc form-data :temp-id temp-id :date now)
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [::process-response]
                  :on-failure [::bad-response]}
     :db (assoc db :loading? true)}))

(re-frame/reg-event-db
  ::set-input-value
  (fn [db [_ key value]]
    (update-in db [:form-input key] (constantly value))))
