(ns ^:figwheel-hooks hcc.innovonto.icv.frontend.ideator.core
    (:require
      [reagent.core :as reagent]
      [re-frame.core :as re-frame]
      [hcc.innovonto.icv.frontend.common.util.reframetimer :as reframetimer]
      [hcc.innovonto.icv.frontend.ideator.events :as events]
      [hcc.innovonto.icv.frontend.ideator.views :as views]
      [hcc.innovonto.icv.frontend.ideator.config :as config]))

;;TODO refactor: timer component that registers its own dispatch function
(defonce do-timer (js/setInterval #(re-frame/dispatch [::reframetimer/timer]) 1000))


(defn dev-setup []
      (when config/debug?
        ;;(enable-console-print!)
        (println "dev mode")))

(defn mount-root []
      (re-frame/clear-subscription-cache!)
      (reagent/render [views/brainstorming-session-main]
                      (.getElementById js/document "app")))

(defn setup-js-functions []
      (.addEventListener js/window "visibilitychange"
                         (fn [event]
                           (case (.-visibilityState js/document)
                             "hidden"  (re-frame/dispatch [::events/session-blurred])
                             "visible" (re-frame/dispatch [::events/session-focused])
                             "default" (re-frame/dispatch [::events/session-blurred])))))

(defn ^:export init []
      (enable-console-print!)
      (re-frame/dispatch-sync [::events/initialize-db])
      (dev-setup)
      (setup-js-functions)
      (mount-root))

(defonce init? (init))