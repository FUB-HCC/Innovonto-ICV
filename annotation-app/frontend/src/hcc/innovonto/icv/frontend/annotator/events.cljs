(ns hcc.innovonto.icv.frontend.annotator.events
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [hcc.innovonto.icv.frontend.common.tracking.core :as tracking]
            [hcc.innovonto.icv.frontend.common.events :as icv-events]
            [hcc.innovonto.icv.frontend.annotator.db :as db]
            [hcc.innovonto.icv.frontend.annotator.config :as config]))


;; DEBUG EVENTS
(re-frame/reg-event-db
 ::debug-print-db
 (fn [db _]
   (do
     (println db)
     db)))

;;Init DB:
;;TODO load assignment-configuration from html
(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (let [hit-config       (js->clj js/hitConfig :keywordize-keys true)
         annotator-config (js->clj js/annotatorConfig :keywordize-keys true)]
     (-> db/default-db
         (assoc :config hit-config)
         (assoc :annotator-config annotator-config)))))

(defn was-last-submit? [current-idea-index ideas]
  (= current-idea-index (- (count ideas) 1)))

(re-frame/reg-event-fx
 ::save-concept-validation-successful
 (fn [{:keys [db]} _]
   (let [current-idea-index (:current-idea-index (:annotator-config db))
         ideas              (:texts (:annotator-config db))]
     (println
      (str "Saved Concept Validation: moving on to: " current-idea-index " : " (count ideas)))
     (if (was-last-submit? current-idea-index ideas)
       {:dispatch [::redirect-to-survey-page]}
       {:dispatch [::load-icv-for-idea (inc current-idea-index)]}))))

(re-frame/reg-event-fx
 ::save-concept-validation-failed
 (fn [{:keys [db]} _]
   (let [current-idea-index (:current-idea-index (:annotator-config db))
         ideas              (:texts (:annotator-config db))]
     (println
      (str "Saving Concept Validation FAILED!: moving on to: " current-idea-index " : " (count ideas)))
     (if (was-last-submit? current-idea-index ideas)
       {:dispatch [::redirect-to-survey-page]}
       {:dispatch [::load-icv-for-idea (inc current-idea-index)]}))))


(re-frame/reg-event-fx
 ::submit-concept-validation
 (fn [{:keys [db]} _]
   (let [current-idea-index (:current-idea-index (:annotator-config db))
         current-idea       (get (:texts (:annotator-config db)) current-idea-index)
         icv-result         (icv-events/to-result (:icv db))
         backend-config     (icv-events/to-backend-config (:config db))]
     {:db         (-> db
                      (assoc-in [:icv :state] "LOADING")
                      (assoc-in [:sync-state] :loading)
                      (assoc-in [:annotator-config :texts current-idea-index :state] "handled"))
      :http-xhrio {:method          :post
                   :uri             (:submit config/urlconfig)
                   :format          (ajax/json-request-format)
                   :params          {:icvResult icv-result
                                     :config    backend-config
                                     :input     current-idea}
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::save-concept-validation-successful]
                   :on-failure      [::save-concept-validation-failed]}})))

;;TODO set state to loading
(re-frame/reg-event-fx
 ::load-icv-for-idea
 (fn [{:keys [db]} [_ text-index]]
   (let [idea (get (:texts (:annotator-config db)) text-index)]
     (do
       (println (str "Loading ICV for Idea: " idea))
       {:db       (assoc-in db [:annotator-config :current-idea-index] text-index)
        :dispatch [::icv-events/annotate-request (:text idea)]}))))


(defn redirect! [loc]
  (set! (.-location js/window) loc))

;;TODO secretary.
(re-frame/reg-event-db
 ::redirect-to-survey-page
 (fn [db _]
   (do
     (redirect! "survey.html"))
   db))