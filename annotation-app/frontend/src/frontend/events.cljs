(ns frontend.events
  (:require
    [re-frame.core :as rf :refer [reg-event-db reg-event-fx]]
    [day8.re-frame.http-fx]
    [ajax.core :as ajax]
    [akiroz.re-frame.storage :refer [reg-co-fx!]]
    [frontend.db :as db]
    [frontend.config :as config]
    [hcc.innovonto.icv.frontend.annotator.events :as annotator-events]))

;;Local Storage Handling:
(reg-co-fx! :mturk-icv-app
            {:fx   :store
             :cofx :store})

(reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))



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

(defn compare-and-update-mturk-state [db store request-mturk-metadata]
  (let [reload (same-hwap? request-mturk-metadata (:mturk-metadata store))]
    (if reload
      ;;RELOAD:
      {:db (-> db
               (assoc :active-page :home)
               (assoc :preview-state :reload)
               (assoc :mturk-metadata request-mturk-metadata)
               (assoc :batch (:batch store)))}
      ;;INIT
      ;;TODO request batch here? eigentlich ja. und am besten mit "loading" indicator.
      {
       :db         (-> db
                       (assoc :active-page :home)
                       (assoc :preview-state :start)
                       (assoc :mturk-metadata request-mturk-metadata))
       :http-xhrio (project-metadata-request (:project-id request-mturk-metadata))
       ;;Initialize the store:
       :store      {:mturk-metadata request-mturk-metadata
                    :batch          (:batch db)
                    }
       })))

;;TODO request batch information.
(defn initialize-home [db mturk-metadata store]
  (let [preview-state (determine-preview-state mturk-metadata)]
    (case preview-state
      :start (compare-and-update-mturk-state db store mturk-metadata)
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
                        (assoc :preview-state preview-state))
                })))

;; usage: (dispatch [:set-active-page {:page :home})
;; triggered when the user clicks on a link that redirects to a another page
;; TODO can i somehow set the req-mturk-metadata before this?
(reg-event-fx
  ::set-active-page
  [(rf/inject-cofx :store)]
  (fn [{:keys [db store]} [_ {:keys [page mturk-metadata]}]]
    (let [set-page (assoc db :active-page page)]
      ;(println (str "Calling init logic for: " page))
      (case page
        :home (initialize-home db mturk-metadata store)
        :annotator {:db set-page :dispatch [::annotator-events/load-icv-for-idea (:current-idea-index (:batch db))]}
        {:db set-page}))))
