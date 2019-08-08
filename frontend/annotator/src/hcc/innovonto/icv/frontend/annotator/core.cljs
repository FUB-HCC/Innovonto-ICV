(ns hcc.innovonto.icv.frontend.annotator.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [hcc.innovonto.icv.frontend.annotator.events :as events]
    [hcc.innovonto.icv.frontend.annotator.views :as views]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/annotator-main]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (enable-console-print!)
  (println "Starting Annotation-App")
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/load-icv-for-idea 0])
  (mount-root))

(defonce init? (init))