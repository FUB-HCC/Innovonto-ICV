(ns hcc.innovonto.icv.frontend.ideator.events
  (:require [cognitect.transit :as transit]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [hcc.innovonto.icv.frontend.ideator.config :as config]
            [hcc.innovonto.icv.frontend.common.util.trackingevents :as tracking]
            [hcc.innovonto.icv.frontend.ideator.db :as db]))

;; DEBUG EVENTS
(re-frame/reg-event-db
 ::debug-print-db
 (fn [db _]
   (let [w (transit/writer :json-verbose)]
     (println
       (.getItem js/localStorage (str (:workerId (:config db)) "|" (:hitId (:config db)))))
     (println db)
     db)))

(re-frame/reg-event-db
 ::debug-show-modal
 (fn [db _]
   (update-in db [:modal] assoc :show? true)))

;;FOCUS/BLUR HANDLING
(re-frame/reg-event-fx
 ::session-blurred
 (fn [{:keys [db]} [_ a]]
   (do
     ;;(println (str "Hidden! " db))
     ;;url event-type payload timer-value
     {:http-xhrio (tracking/track {:type "session-blurred" :payload {:timerValue (:seconds (:timer db))}} config/urlconfig)
      :dispatch   [:hcc.innovonto.icv.frontend.common.util.reframetimer/stop-timer]})))

(re-frame/reg-event-fx
 ::session-focused
 (fn [{:keys [db]} [_ a]]
   (do
     (println (str "Visible! " db))
     {:http-xhrio (tracking/track {:type "session-focused" :payload {:timerValue (:seconds (:timer db))}} config/urlconfig)
      :dispatch   [:hcc.innovonto.icv.frontend.common.util.reframetimer/start-timer]})))


;; INITIALIZATION
;;TODO move this into the timer namespace
(defn get-timer-value [config]
  (let [local-storage-result (.getItem js/localStorage (str (:workerId config) "|" (:hitId config)))]
    (do
      ;;(println (str "initial timer value: " (:initialTimerValue config)))
      ;;(println (str "result: " local-storage-result))
      (if (nil? local-storage-result)
        (:initialTimerValue config)
        (js/parseInt local-storage-result)))))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   (let [hit-config       (js->clj js/hitConfig :keywordize-keys true)
         challenge-config (js->clj js/challengeConfig :keywordize-keys true)
         previous-ideas   (js->clj js/previousIdeas)]
     (-> db/default-db
         (assoc :config hit-config)
         (assoc :challenge-config challenge-config)
         (assoc :submitted-ideas previous-ideas)
         (assoc :timer
                {:state         "running"
                 :total-seconds (:initialTimerValue hit-config)
                 :seconds       (get-timer-value hit-config)})))))

;; IDEA EVENTS
;;TODO stimmt das?
(defn allocate-next-id
  [ideas]
  ((fnil inc 0) (inc (count ideas))))

(re-frame/reg-event-fx
  ::idea-form-submit
 (fn [{:keys [db]} [event idea-description]]
   (let [id (allocate-next-id (:submitted-ideas db))]
     { ;;:http-xhrio (tracking/track {:type "text-submit" :payload {:id id :text idea-description} :timerValue (:seconds (:timer db))})
      :dispatch [:hcc.innovonto.icv.frontend.common.events/annotate-request idea-description]})))

;;TODO decomplect idea-submit and tracking event
(re-frame/reg-event-fx
 ::idea-form-submit-without-icv
 (fn [{:keys [db]} [event idea-description]]
   (do
     (println (str "idea-form-submit:" event " description:" idea-description))
     (let [id (allocate-next-id (:submitted-ideas db))]
       {:http-xhrio (tracking/track {:type "idea-submit" :payload {:id id :text idea-description} :timerValue (:seconds (:timer db))} config/urlconfig)
        :db         (update-in db [:submitted-ideas] conj {:id id :text idea-description})}))))

;;INSPIRATION EVENTS
(re-frame/reg-event-db
 ::loading-inspirations-failed
 []
 (fn [db event]
   (update-in db [:inspirations] assoc :state "ERROR")))

(re-frame/reg-event-db
 ::loading-inspirations-successful
 (fn [db [event response]]
   (do
     (println (str "Got the following inspirations from the server: " response))
     (-> db
         (update-in [:inspirations] assoc :ideas (js->clj response))
         (update-in [:inspirations] assoc :state "INITIAL")))))

(re-frame/reg-event-fx
 ::request-random-inspiration
 (fn [{:keys [db]} [_]]
   {:db         (update-in db [:inspirations] assoc :state "LOADING")
    :http-xhrio {:method          :get
                 :uri             (str (:get-inspirations config/urlconfig) "?timerValue=" (:seconds (:timer db)))
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::loading-inspirations-successful]
                 :on-failure      [::loading-inspirations-failed]}}))

(re-frame/reg-event-fx
 ::request-far-inspiration
 (fn [{:keys [db]} [_]]
   {:db         (update-in db [:inspirations] assoc :state "LOADING")
    :http-xhrio {:method          :get
                 :uri             (str (:get-inspirations config/urlconfig) "?timerValue=" (:seconds (:timer db)) "&strategy=far")
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::loading-inspirations-successful]
                 :on-failure      [::loading-inspirations-failed]}}))

(re-frame/reg-event-fx
 ::request-near-inspiration
 (fn [{:keys [db]} [_]]
   {:db         (update-in db [:inspirations] assoc :state "LOADING")
    :http-xhrio {:method          :get
                 :uri             (str (:get-inspirations config/urlconfig) "?timerValue=" (:seconds (:timer db)) "&strategy=near")
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::loading-inspirations-successful]
                 :on-failure      [::loading-inspirations-failed]}}))


