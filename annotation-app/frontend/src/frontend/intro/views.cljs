(ns frontend.intro.views
  (:require [re-frame.core :as rf]
            [frontend.subs :as subs]
            [antizer.reagent :as ant]))

(defn invalid-alert []
  [ant/alert {:type "warning" :message "Something went wrong with loading the HIT data. Please try to reload your browser window. If the error persists, please contact us via the requester contact interface."}])

(defn preview-alert []
  [ant/alert {:type "info" :message "This is the preview page! Please accept the HIT before working on the question! The Ideas here a just examples, you will get different ideas once you accept the hit."}])

(defn reload-alert []
  [ant/alert {:type "info" :message "This page was reloaded. All annotations done before are saved and will be skipped."}])

(defn alert-for [preview-state]
  (case preview-state
    :start [reload-alert]
    :preview [preview-alert]
    [invalid-alert]))

(defn task-metadata []
  (let [project-metadata @(rf/subscribe [::subs/project-metadata])]
    (if (some? project-metadata)
      [:div
       [:ul
        [:li "You will have to annotate " [:strong (get project-metadata :batch-size)] " ideas."]
        [:li "You will receive " [:strong (str "$" (get project-metadata :compensation))] " for this task."]
        [:li "This task will take about " [:strong (get project-metadata :estimated-time-in-minutes)] " minutes."]]]
      [:div])))

(defn task-screenshot []
  [:span "Here be screenshot"])

;;(not= preview-state :start)
(defn start-button [preview-state]
  [ant/row
   [ant/button {:size "large" :type "primary" :disabled (not= preview-state :start)}
    [:a {:href "#/tutorial"} "Start"]]])

(defn intro []
  (let [preview-state @(rf/subscribe [::subs/preview-state])]
    [ant/row
     [:div
      [alert-for preview-state]
      [:h1 "Annotate Idea Texts"]
      [:div
       [:h2 "Task Description"]
       [:p "In different studies we collected idea texts for different challenges. Your task today is to determine
       which terms (concepts) are used within these texts by selecting the most correct term for a word from a list."]]
      [task-metadata]
      [task-screenshot]
      [start-button preview-state]]]))
