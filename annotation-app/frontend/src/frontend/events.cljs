(ns frontend.events
  (:require
    [frontend.db :as db]
    [day8.re-frame.http-fx]
    [ajax.core :as ajax]
    [re-frame.core :refer [reg-event-db reg-event-fx]]))

(def base-URL "http://localhost:8004/api/mturk")

(reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(defn determine-preview-state [{:keys [project-id hit-id worker-id assignment-id turk-submit-to]}]
  (println "Keys: " project-id hit-id worker-id assignment-id turk-submit-to)
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
        (assoc :loading false))))

;;TODO implement failure
(reg-event-db
  ::project-metadata-failed
  (fn [db [_ result]]
    ;; result is a map containing details of the failure
    (assoc db :failure-http-result result)))

(defn project-metadata-request [project-id]
  {:method          :get
   :uri             (str base-URL "/projectMetadata")
   :params          {:projectId project-id}
   :timeout         8000                                    ;; optional see API docs
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      [::project-metadata-successful]
   :on-failure      [::project-metadata-failed]})

(defn prepare-home-page [db mturk-metadata]
  (let [preview-state (determine-preview-state mturk-metadata)]
    (case preview-state
      :start {
              :db         (-> db
                              (assoc :active-page :home)
                              (assoc :preview-state preview-state)
                              ;TODO check if there is state in local storage for HWA/P
                              ;TODO check if there is state in local storage for the project metadata for this project.
                              ;TODO if there is metadata, we don't have to reload it and can skip the http-xhrio
                              (assoc :project-metadata {
                                                        :batch-size                "?"
                                                        :compensation              "?"
                                                        :estimated-time-in-minutes "?"
                                                        }))
              :http-xhrio (project-metadata-request (:project-id mturk-metadata))
              }
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
(reg-event-fx
  ::set-active-page
  (fn [{:keys [db]} [_ {:keys [page mturk-metadata]}]]
    (let [set-page (assoc db :active-page page)]
      (case page
        :home (prepare-home-page db mturk-metadata)
        {:db set-page}))))
