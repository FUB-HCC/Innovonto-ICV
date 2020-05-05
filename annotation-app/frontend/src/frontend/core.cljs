(ns ^:figwheel-hooks frontend.core
  (:require
    [goog.dom :as gdom]
    [reagent.dom :as rdom]
    [re-frame.core :as rf]
    [frontend.router :as router]
    [frontend.events]
    [frontend.subs]
    [frontend.views :as views]))

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (rdom/render [views/annotation-app] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(defn init! []
  (router/setup-app-routes!)
  (rf/dispatch-sync [::frontend.events/initialize-db])
  (mount-app-element))

(init!)

(defn ^:after-load on-reload []
  ;;(rf/clear-subscription-cache!)
  (mount-app-element))
