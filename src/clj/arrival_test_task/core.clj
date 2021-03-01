(ns arrival-test-task.core
  (:require [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [com.stuartsierra.component :as component]
            [arrival-test-task.system :refer [->system]]
            [datomic.api :as d])
  (:gen-class))

(defonce config
  {:jetty {:port (or (env :port) 8087)
           :join? false}
   :datomic {:uri "datomic:free://localhost:4334/testdb?password=datomic"}})

(defn -main [& _args]
  (let [system (->system config)
        db-uri (get-in config [:datomic :uri])]
    (d/delete-database db-uri)
    (d/create-database db-uri)
    (component/start system)
    (.addShutdownHook
      (Runtime/getRuntime)
      (Thread. #(component/stop system)))))
