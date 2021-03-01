(ns arrival-test-task.system
  (:require [com.stuartsierra.component :as component]
            [arrival-test-task.server :as server]
            [arrival-test-task.handler :as handler]
            [arrival-test-task.db.datomic :as datomic]))

(defn ->system
  [config]
  (component/system-using
    (component/system-map
      :config  config
      :db      (datomic/create)
      :server  (server/create)
      :handler (handler/create))
    {:server  [:config :handler]
     :db      [:config]
     :handler [:db]}))