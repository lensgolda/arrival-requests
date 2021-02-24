(ns arrival-test-task.cofxs
  (:require [re-frame.cofx :as rfc]))


(defonce temp-id (atom 0))

(rfc/reg-cofx
  ::now
  (fn [cofx _]
    (assoc cofx :now (js/Date.))))

(rfc/reg-cofx
  ::temp-id
  (fn [cofx _]
    (assoc cofx :temp-id (swap! temp-id inc))))