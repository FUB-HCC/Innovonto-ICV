(ns frontend.survey.events
  (:require [re-frame.core :as rf]
            [frontend.config :as config]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [clojure.set :as set]))

(rf/reg-fx
  :submit-form
  (fn [form-id]
    (-> js/document
        (.getElementById form-id)
        (.submit))))

;;TODO post request to backend
;;Set state to ready?
(rf/reg-event-fx
  ::annotation-batch-submission-successful
  (fn [coeffect _]
    {
     :submit-form "mturkForm"
     }))

(rf/reg-event-fx
  ::annotation-batch-submission-failed
  (fn [coeffect event]
    (do
      (println (str "Annotation Batch Submission failed: " event))
      {
       ;TODO how to handle this?
       })
    ))

(defn to-annotation-submit [result-item]
  (set/rename-keys result-item {:text :content :result :annotations}))

(defn get-batch-submission-data [db]
  (let [mturk-metadata (:mturk-metadata db)
        results (:texts (:batch db))
        events nil]
    ;(println (str "Results are: " results))
    {
     :projectId            (:project-id mturk-metadata)
     :hitId                (:hit-id mturk-metadata)
     :workerId             (:worker-id mturk-metadata)
     :assignmentId         (:assignment-id mturk-metadata)
     :fulltextFeedback     nil
     :clarityRating        0
     :passedAttentionCheck false
     :annotatedIdeas       (into [] (map to-annotation-submit results))
     ;;TODO events
     }))

(rf/reg-event-fx
  ::annotation-batch-submit
  (fn [{:keys [db]}]
    (do
      (println "Annotation Batch Submit!")
      {
       ;;TODO set db state to loading.
       :http-xhrio {:method          :post
                    :uri             (:submit config/urlconfig)
                    :format          (ajax/json-request-format)
                    :response-format (ajax/json-response-format {:keywords? true})
                    :params          (get-batch-submission-data db)
                    :on-success      [::annotation-batch-submission-successful]
                    :on-failure      [::annotation-batch-submission-failed]}})))