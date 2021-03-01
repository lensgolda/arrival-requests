(ns arrival-test-task.db.datomic
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(def requests-schema [{:db/ident :req/header
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "Request header"}
                      {:db/ident :req/description
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "Request description"}
                      {:db/ident :req/applicant
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "Request applicant"}
                      {:db/ident :req/performer
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "Request performer"}
                      {:db/ident :req/date
                       :db/valueType :db.type/instant
                       :db/cardinality :db.cardinality/one
                       :db/doc "Request header"}])

(def all-req-query '[:find (pull ?e [*])
                     :where [?e :req/header]])

(defrecord Datomic []
  component/Lifecycle
  (start [this]
    (let [db-uri     (get-in this [:config :datomic :uri])
          _          (assert db-uri "db-uri must be provided")
          connection (d/connect db-uri)]
      (d/transact connection requests-schema)
      (assoc this :datasource connection)))
  (stop [this]
    (assoc this :datasource nil)))

(defn create []
  (->Datomic))