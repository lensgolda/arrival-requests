(ns arrival-test-task.handler
  (:require
    [compojure.core :refer [GET POST defroutes]]
    [compojure.route :refer [resources not-found]]
    [ring.util.response :as r]
    [ring.middleware.reload :refer [wrap-reload]]
    [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
    [ring.middleware.cors :refer [wrap-cors]]
    [shadow.http.push-state :as push-state]))


(defonce in-memory-db (atom {:items []}))
(defonce serial-id (atom 0))

(defroutes routes
  (GET "/" _ (r/resource-response "index.html" {:root "public"}))
  (GET "/list" _
    (r/response @in-memory-db))
  (POST "/list" request
    (let [response (as-> (:body request) $r
                         (assoc $r :id (swap! serial-id inc))
                         (swap! in-memory-db update :items conj $r))]
      (r/response response)))
  (resources "/")
  (not-found "Page not found"))

(def dev-handler (-> #'routes wrap-reload push-state/handle))

(def handler
  (-> routes
      #_wrap-json-params
      (wrap-json-body {:keywords? true :bigdecimals? true})
      wrap-json-response
      (wrap-cors :access-control-allow-origin #"http://localhost:8280"
                 :access-control-allow-headers ["Origin" "X-Requested-With" "Content-Type" "Accept"]
                 :access-control-allow-methods [:get :put :post :delete :options])))
