(ns frontend.events
  (:require
    [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
    [day8.re-frame.http-fx]
    [ajax.core :as ajax]
    [akiroz.re-frame.storage :refer [reg-co-fx!]]
    [frontend.db :as db]
    [frontend.config :as config]
    [hcc.innovonto.icv.frontend.annotator.events :as annotator-events]
    [clojure.set :as set]
    [clojure.string :as string]))

;;Local Storage Handling:
(reg-co-fx! :mturk-icv-app
            {:fx   :store
             :cofx :store})

(reg-event-fx
  ::initialize-db
  [(rf/inject-cofx :store)]
  (fn [{:keys [_ store]} _]
    {:db (-> db/default-db
             (assoc :mturk-metadata (:mturk-metadata store))
             (assoc :batch (:batch store))
             ;TODO project metadata?
             ;TODO icv?
             ;TODO tracking events?
             ;TODO survey?
             )}))

(defn determine-preview-state [{:keys [project-id hit-id worker-id assignment-id turk-submit-to]}]
  ;; START
  (if (every? some? [project-id hit-id worker-id assignment-id turk-submit-to])
    :start
    ;;PREVIEW
    (if (and (some? project-id) (some? hit-id) (= assignment-id "ASSIGNMENT_ID_NOT_AVAILABLE"))
      :preview
      ;;ELSE
      :invalid)))

(reg-event-db
  ::project-metadata-successful
  (fn [db [_ result]]
    (-> db
        (assoc :project-metadata {
                                  :batch-size                (get result :batchSize)
                                  :compensation              (get result :compensation)
                                  :estimated-time-in-minutes (get result :estimatedTimeInMinutes)})
        (assoc :sync-state :up-to-date))))

;;TODO implement failure
(reg-event-db
  ::project-metadata-failed
  (fn [db [_ result]]
    (-> db
        (assoc :sync-state :up-to-date)
        (assoc :failure-http-result result))))

(defn project-metadata-request [project-id]
  {:method          :get
   :uri             (:project-metadata config/urlconfig)
   :params          {:projectId project-id}
   :timeout         8000                                    ;; optional see API docs
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      [::project-metadata-successful]
   :on-failure      [::project-metadata-failed]})

(defn same-hwap? [left right]
  (and (= (:hit-id left) (:hit-id right))
       (= (:worker-id left) (:worker-id right))
       (= (:assignment-id left) (:assignment-id right))
       (= (:project-id left) (:project-id right))))

(reg-event-fx
  ::batch-request-successful
  [(rf/inject-cofx :store)]
  (fn [{:keys [db store]} [_ result]]
    (let [ideas (:ideas result)
          batch {:current-idea-index 0
                 :texts              (into [] (map #(set/rename-keys %1 {:content :text}) ideas))
                 :challenges         (:challenges result)}]
      {
       :db    (-> db
                  (assoc :batch batch)
                  (assoc :project-metadata {
                                            :batch-size                (get result :batchSize)
                                            :compensation              (get result :compensation)
                                            :estimated-time-in-minutes (get result :estimatedTimeInMinutes)})
                  (assoc :sync-state :up-to-date))
       :store (-> store
                  (assoc :batch batch))
       })))

;;TODO implement failure
(reg-event-db
  ::batch-request-failed
  (fn [db [_ result]]
    (-> db
        (assoc :sync-state :up-to-date)
        (assoc :failure-http-result result))))

(defn batch-request-for [mturk-metadata]
  {:method          :get
   :uri             (:annotation-batch config/urlconfig)
   :params          {:projectId    (:project-id mturk-metadata)
                     :hitId        (:hit-id mturk-metadata)
                     :workerId     (:worker-id mturk-metadata)
                     :assignmentId (:assignment-id mturk-metadata)}
   :timeout         8000                                    ;; optional see API docs
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      [::batch-request-successful]
   :on-failure      [::batch-request-failed]})

(defn compare-and-update-mturk-state [db request-mturk-metadata]
  (let [reload (same-hwap? request-mturk-metadata (:mturk-metadata db))]
    (if reload
      ;;RELOAD:
      {:db (-> db
               (assoc :active-page :home)
               (assoc :preview-state :reload))}
      ;;INIT
      {
       :db         (-> db
                       (assoc :sync-state :loading)
                       (assoc :active-page :home)
                       (assoc :preview-state :start)
                       (assoc :mturk-metadata request-mturk-metadata)
                       ;;TODO Clear old stuff
                       )
       :http-xhrio (batch-request-for request-mturk-metadata)
       ;;Initialize the store: This overwrites the old store state:
       :store      {:mturk-metadata request-mturk-metadata}
       })))

(defn check-empty [[key value]]
  (if (string/blank? value)
    (str "Could not determine " (name key))
    ""))

(defn validate-hwap [mturk-metadata]
  (let [error-messages (map check-empty (seq mturk-metadata))]
    (string/trim (string/join ", " error-messages))))

(defn initialize-home [db mturk-metadata]
  (let [preview-state (determine-preview-state mturk-metadata)
        preview-state-error-message (validate-hwap mturk-metadata)]
    (case preview-state
      :start (compare-and-update-mturk-state db mturk-metadata)
      :preview {
                :db         (-> db
                                (assoc :active-page :home)
                                (assoc :preview-state preview-state)
                                ;TODO check if there is state in local storage for the project metadata for this project.
                                ;TODO if there is metadata, we don't have to reload it and can skip the http-xhrio
                                (assoc :project-metadata {
                                                          :batch-size                "?"
                                                          :compensation              "?"
                                                          :estimated-time-in-minutes "?"
                                                          }))
                :http-xhrio (project-metadata-request (:project-id mturk-metadata))
                }
      :invalid {
                ;;TODO remove project-metadata?
                :db (-> db
                        (assoc :active-page :home)
                        (assoc :preview-state preview-state)
                        (assoc :last-error preview-state-error-message))
                })))

;; usage: (dispatch [:set-active-page {:page :home})
;; triggered when the user clicks on a link that redirects to a another page
;; TODO can i somehow set the req-mturk-metadata before this?
(reg-event-fx
  ::set-active-page
  (fn [{:keys [db]} [_ {:keys [page mturk-metadata]}]]
    (let [set-page (assoc db :active-page page)]
      ;(println (str "Calling init logic for: " page))
      (case page
        :home (initialize-home db mturk-metadata)
        :annotator {:db set-page :dispatch [::annotator-events/load-icv-for-idea (:current-idea-index (:batch db))]}
        {:db set-page}))))
