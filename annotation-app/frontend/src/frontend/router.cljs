(ns frontend.router
  (:require-macros [secretary.core :refer [defroute]])
  (:import [goog History]
           [goog.history EventType])
  (:require [secretary.core :as secretary]
            [goog.events :as gevents]
            [re-frame.core :refer [dispatch]]
            [frontend.events :as events]))

(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn get-mturk-metadata [query-params]
  {
   :annotation-project-id (:annotationProjectId query-params)
   :hit-id                (:hitId query-params)
   :worker-id             (:workerId query-params)
   :assignment-id         (:assignmentId query-params)
   :turk-submit-to        (:turkSubmitTo query-params)
   })

(defn setup-app-routes! []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute "/" [query-params]
            (dispatch [::events/set-active-page {:page :home :mturk-metadata (get-mturk-metadata query-params)}]))

  (defroute "/tutorial" []
            (dispatch [::events/set-active-page {:page :tutorial}]))

  (defroute "/annotator" []
            (dispatch [::events/set-active-page {:page :annotator}]))

  (defroute "/thank-you" []
            (dispatch [::events/set-active-page {:page :thank-you}]))
  (hook-browser-navigation!))