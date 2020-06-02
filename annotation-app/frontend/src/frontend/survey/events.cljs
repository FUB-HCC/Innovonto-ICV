(ns frontend.survey.events
  (:require [re-frame.core :as rf]
            [frontend.config :as config]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [clojure.set :as set]))

(rf/reg-event-db
  ::set-clarity-rating
  (fn [db [_ new-value]]
    (assoc-in db [:survey :clarity-rating] new-value)))

(rf/reg-event-db
  ::set-fulltext-feedback
  (fn [db [_ new-value]]
    (assoc-in db [:survey :fulltext-feedback] new-value)))

(rf/reg-fx
  :submit-form
  (fn [form-id]
    (-> js/document
        (.getElementById form-id)
        (.submit))))

;;Set state to ready?
(rf/reg-event-fx
  ::annotation-batch-submission-successful
  (fn [{:keys [db]} _]
    {
     :db          (assoc db :sync-state :loading)
     :submit-form "mturkForm"
     }))

(rf/reg-event-fx
  ::annotation-batch-submission-failed
  (fn [{:keys [db]} event]
    (println (str "Annotation Batch Submission failed: " event))
    {
     :db (-> db
             (assoc :sync-state :up-to-date)
             (assoc :last-error (:last-error event)))
     }))

(defn to-annotation-submit [result-item]
  (set/rename-keys result-item {:text :content :result :annotations}))

(defn get-batch-submission-data [db]
  (let [mturk-metadata (:mturk-metadata db)
        results (:texts (:batch db))
        events (:tracking-events db)]
    {
     :projectId            (:project-id mturk-metadata)
     :hitId                (:hit-id mturk-metadata)
     :workerId             (:worker-id mturk-metadata)
     :assignmentId         (:assignment-id mturk-metadata)
     :fulltextFeedback     (:fulltext-feedback (:survey db))
     :clarityRating        (:clarity-rating (:survey db))
     ;;TODO implement attention-check logic.
     :passedAttentionCheck true
     :annotatedIdeas       (into [] (map to-annotation-submit results))
     :trackingEvents               events
     }))

(rf/reg-event-fx
  ::annotation-batch-submit
  (fn [{:keys [db]}]
    {
     :db         (assoc db :sync-state :loading)
     :http-xhrio {:method          :post
                  :uri             (:submit config/urlconfig)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :params          (get-batch-submission-data db)
                  :on-success      [::annotation-batch-submission-successful]
                  :on-failure      [::annotation-batch-submission-failed]}}))