(ns arrival-test-task.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::app-view
  (fn [db]
    (:view-type db)))

(re-frame/reg-sub
  ::requests-list
  (fn [db]
    (:requests db)))

(re-frame/reg-sub
  ::initialized?
  (fn [db _]
    (not (seq db))))

(re-frame/reg-sub
  ::form-data
  (fn [db _]
    (:form-input db)))

(re-frame/reg-sub
  ::loading?
  (fn [db _]
    (:loading? db)))
