(ns hcc.innovonto.icv.frontend.common.util.reframetimer
  (:require [re-frame.core :as re-frame]
            [hcc.innovonto.icv.frontend.common.util.trackingevents :as tracking]))

;; DEBUG TIMER EVENTS
(re-frame/reg-event-db
 ::start-timer
 []
 (fn [db _]
   (update-in db [:timer] assoc :state "running")))

(re-frame/reg-event-db
 ::stop-timer
 []
 (fn [db _]
   (update-in db [:timer] assoc :state "stopped")))

(re-frame/reg-event-db
 ::debug-reset-timer
 (fn [db _]
   (do
     (update-in db [:timer] assoc :seconds (:initialTimerValue (:config db))))))

;;TIMER HANDLING
(defn timer-running [db]
  (= (:state (:timer db)) "running"))

(defn timer-done [db]
  (<= (:seconds (:timer db)) 0))

(re-frame/reg-event-fx
 ::timer
 (fn [{db :db} [_]]
   (if (timer-running db)
     (do
       (.setItem js/localStorage (str (:workerId (:config db)) "|" (:hitId (:config db))) (:seconds (:timer db)))
       (if (timer-done db)
         {:dispatch-n [[::time-is-up {:show? true}] [::stop-timer]]}
         {:db (update-in db [:timer :seconds] dec)}))
     {})))

;;what are the effects of "time is up"
;;1. show the modal
;;2. send the data to the server
;;3. set the timer to finished
;;TODO this dispatches an ajax request if the time is up and the user switches tabs
;;TODO this is a dependency between brainstorming app and timer
(re-frame/reg-event-fx
 ::time-is-up
 (fn [{db :db} [_ data]]
   {:http-xhrio (tracking/track {:type "session-finished" :payload db :timerValue 0} (:url-config db))
    :db (-> db
            (assoc :modal data)
            (assoc :sync-state :loading))}))
