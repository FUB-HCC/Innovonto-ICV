(ns hcc.innovonto.icv.frontend.annotator.events
  (:require [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [hcc.innovonto.icv.frontend.common.tracking.core :as tracking]
            [hcc.innovonto.icv.frontend.common.events :as icv-events]
            [frontend.config :as config]))


;; DEBUG EVENTS
(rf/reg-event-db
  ::debug-print-db
  (fn [db _]
    (do
      (println db)
      db)))

(defn was-last-submit? [current-idea-index ideas]
  (= current-idea-index (- (count ideas) 1)))

(rf/reg-event-fx
  ::save-concept-validation-successful
  (fn [{:keys [db]} _]
    (let [current-idea-index (:current-idea-index (:batch db))
          ideas (:texts (:batch db))]
      (println
        (str "Saved Concept Validation: moving on to: " current-idea-index " : " (count ideas)))
      (if (was-last-submit? current-idea-index ideas)
        {:dispatch [:frontend.events/set-active-page {:page :thank-you}]}
        {:dispatch [::load-icv-for-idea (inc current-idea-index)]}))))

(rf/reg-event-fx
  ::submit-concept-validation
  [(rf/inject-cofx :store)]
  (fn [{:keys [db store]} _]
    (let [current-idea-index (:current-idea-index (:batch db))
          icv-result (icv-events/to-result (:icv db))]
      {:db       (-> db
                     (assoc-in [:icv :state] "LOADING")
                     (assoc-in [:sync-state] :loading)
                     (assoc-in [:batch :texts current-idea-index :state] "handled")
                     (assoc-in [:batch :texts current-idea-index :result] (:annotations icv-result)))
       :store    (-> store
                     (assoc-in [:batch :texts current-idea-index :state] "handled")
                     (assoc-in [:batch :texts current-idea-index :result] (:annotations icv-result)))
       :dispatch [::save-concept-validation-successful]})))

(rf/reg-event-fx
  ::load-icv-for-idea
  [(rf/inject-cofx :store)]
  (fn [{:keys [db store]} [_ text-index]]
    (let [idea (get (:texts (:batch db)) text-index)]
      (do
        (println (str "Loading ICV for Idea: " idea))
        {:db       (-> db
                       (assoc :sync-state :loading)
                       (assoc-in [:batch :current-idea-index] text-index))
         :store    (-> store
                       (assoc-in [:batch :current-idea-index] text-index))
         :dispatch [::icv-events/annotate-request (:text idea)]}))))