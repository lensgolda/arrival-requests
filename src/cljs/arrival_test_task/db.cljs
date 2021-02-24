(ns arrival-test-task.db)

(def default-db
  {:view-type :list ;; (or :form :list)
   :requests []
   :form-input {}
   :loading? false})
