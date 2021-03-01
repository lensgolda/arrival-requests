(ns arrival-test-task.handler
  (:require
   [compojure.core :refer [GET POST PUT] :as compojure]
   [compojure.route :refer [resources not-found]]
   [ring.util.response :as r]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
   [ring.middleware.cors :refer [wrap-cors]]
   [shadow.http.push-state :as push-state]
   [com.stuartsierra.component :as component]
   [datomic.api :as d]
   [arrival-test-task.db.datomic :as db]))


(defonce in-memory-db (atom {:items []}))
(defonce serial-id (atom 0))

(defn- routes
  [{{<db> :datasource} :db}]
  (compojure/routes
    ;; Single page index.html
    (GET "/" _ (r/resource-response "index.html" {:root "public"}))

    ;; in memory storage
    (GET "/list" _
      (r/response @in-memory-db))
    (POST "/list" request
      (let [response (as-> (:body request) $r
                       (assoc $r :id (swap! serial-id inc))
                       (swap! in-memory-db update :items conj $r))]
        (r/response response)))

    ;; datomic storage / list requests endpoint
    (GET "/datomic-list" _
      (let [data (d/q db/all-req-query (d/db <db>))]
        (r/response {:items (flatten data)})))

    ;; datomic storage / create request endpoint
    (POST "/datomic-create" {body :body :as request}
      (let [{:keys [header description applicant performer date]} body
            tx-data [{:req/header header
                      :req/description description
                      :req/applicant applicant
                      :req/performer performer
                      :req/date (clojure.instant/read-instant-date date)}] ;; or simply #_(java.util.Date.)
            _ (d/transact <db> tx-data)]
        (r/response {})))

    (resources "/")
    (not-found "Page not found")))

(def dev-handler (-> (routes {}) wrap-reload push-state/handle))

(defn- wrap-all
  [handler]
  (-> handler
      (wrap-json-body {:keywords? true :bigdecimals? true})
      wrap-json-response
      (wrap-cors
        :access-control-allow-origin #"http://localhost:8280"
        :access-control-allow-headers [#_"Accept" "Content-Type" #_"User-Agent" #_"Cache-Control"]
        :access-control-allow-methods [:get :post :put :delete :options])))

(defrecord Router [handler-fn]
  component/Lifecycle
  (start [this]
    (assoc this :routes (wrap-all (handler-fn this))))
  (stop [this]
    (assoc this :routes nil)))

(defn create []
  (->Router routes))