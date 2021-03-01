(ns arrival-test-task.core
  (:require [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [com.stuartsierra.component :as component]
            [arrival-test-task.system :refer [->system]]
            [datomic.api :as d]
            [clojure.tools.reader.edn :as edn])
  (:gen-class))


(defn -main [& _args]
  (let [config (edn/read-string (slurp "./config.edn"))
        system (->system config)
        db-uri (get-in config [:datomic :uri])
        _      (assert db-uri "Datomic database URI must be provided")]
    (d/delete-database db-uri)
    (d/create-database db-uri)
    (component/start system)
    (.addShutdownHook
      (Runtime/getRuntime)
      (Thread. #(component/stop system)))))
