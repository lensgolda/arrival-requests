(ns arrival-test-task.server
  (:require [com.stuartsierra.component :as component]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:import (org.eclipse.jetty.server Server)))

(defrecord WebServer []
  component/Lifecycle
  (start [this]
    (let [config  (get-in this [:config :jetty])
          handler (get-in this [:handler :routes] false)
          _       (assert config "config is missed or empty")
          _       (assert handler "handler/routes key not found in system-map")]
      (if (:jetty this)
        this
        (assoc this :jetty (run-jetty handler config)))))
  (stop [this]
    (when-let [jetty (:jetty this)]
      (.stop ^Server jetty))
    (assoc this :jetty nil)))

(defn create []
  (map->WebServer {}))