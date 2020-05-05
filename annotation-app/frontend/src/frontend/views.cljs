(ns frontend.views
  (:require [re-frame.core :as rf]
            [frontend.subs :as subs]
            [antizer.reagent :as ant]))

(defn footer []
  [ant/row
   [:hr]
   [:p.footer
    "Provided by the HCC @ FU Berlin."
    [:br]
    "If you have questions please contact us."
    [:br]
    "  If you can not finish this hit due to errors, please contact us via the mturk interface."]])

(defn invalid-alert []
  [ant/alert {:type "warning" :message "Something went wrong with loading the HIT data. Please try to reload your browser window. If the error persists, please contact us via the requester contact interface."}])

(defn preview-alert []
  [ant/alert {:type "info" :message "This is the preview page! Please accept the HIT before working on the question! The Ideas here a just examples, you will get different ideas once you accept the hit."}])

(defn reload-alert []
  [ant/alert {:type "info" :message "This page was reloaded. All annotations done before are saved and will be skipped."}])

(defn task-metadata []
  [:span "Here be task metadata"])

(defn task-screenshot []
  [:span "Here be screenshot"])

(defn start-button []
  [ant/row
   [ant/button {:size "large"}
    [:a {:href "#/tutorial"} "Start"]]])

(defn home []
  [ant/row
   [:div
    [invalid-alert]
    [:h1 "Idea Annotation App"]
    [:div
     [:h2 "Task Description"]
     [:p "In this task..."]]
    [task-metadata]
    [task-screenshot]
    [start-button]]])

(defn tutorial []
  [ant/row
   [:h1 "Tutorial"]
   [:div
    [:a {:href "#/annotator"} "Go to annotator"]]])

(defn annotator []
  [:h1 "This is annotator"])

(defn thank-you []
  [:h1 "Thank you!"])

(defn pages [page-name]
  (case page-name
    :home [home]
    :tutorial [tutorial]
    :annotator [annotator]
    :thank-you [thank-you]
    [home]))

(defn annotation-app
  []
  (let [active-page @(rf/subscribe [::subs/active-page])]
    [ant/layout
     [ant/layout-content
      [pages active-page]
      [footer]
      ]]))