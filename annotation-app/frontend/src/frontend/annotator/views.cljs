(ns frontend.annotator.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [antizer.reagent :as ant]
            [hcc.innovonto.icv.frontend.common.views :as icv]
            [hcc.innovonto.icv.frontend.common.subs :as icv-subs]
            [hcc.innovonto.icv.frontend.annotator.events :as events]
            [hcc.innovonto.icv.frontend.annotator.subs :as subs]
            [hcc.innovonto.icv.frontend.annotator.config :as config]))

(defn annotation-progress-bar []
  (let [handled-ideas   @(rf/subscribe [::subs/number-handled])
        unhandled-ideas @(rf/subscribe [::subs/number-unhandled])
        percentage-done @(rf/subscribe [::subs/percentage-done])]
    [:div
     [:p "Progress"]
     [ant/progress {:percent percentage-done}]
     [:p
      (str "You have annotated " handled-ideas " Ideas. Ideas left: " unhandled-ideas)]]))

;;TODO states: empty, loading, some, error
(defn concept-validation-panel []
  (let [component-state (:state @(rf/subscribe [::subs/icv]))]
    [ant/card
     {:title (reagent/as-element [:span [ant/icon {:type "check-circle-o"}] "Validation"])
      :type  "inner"}
     (case component-state
       "INITIAL"       [:div "Loading first idea text, please wait..."]
       "LOADING"       [ant/spin {:class-name "spin-centered"}]
       "ANNOTATING"    [icv/select-resource-candidate-menu]
       "ALL-ANNOTATED" [:div "Everything is annotated. Please click submit"]
       [:p "There was an error on the server! Please reset the panel and try again."])]))

(defn annotation-panel []
  [:div
   [ant/row
    {:class-name "annotated-text-container"}
    [:div.annotated-text-textarea
     [icv/chunks-to-hiccup @(rf/subscribe [::icv-subs/chunks])]]]])

(defn annotation-panel-error-state []
  (let [current-idea-index @(rf/subscribe [::subs/current-idea-index])]
    [:div
     [ant/alert
      {:message "There was an error on the server! Please reset the panel and try again."
       :type    "error"}]
     [ant/row
      [:div.annotated-text-textarea
       [:p " "]]]
     [ant/form-item
      [ant/button
       {:html-type "button"
        :type      "secondary"
        :on-click  #(rf/dispatch [::events/load-icv-for-idea current-idea-index])}
       "Reset"]]]))

(defn icv-input-text-panel []
  (let [icv @(rf/subscribe [::subs/icv])]
    [ant/card
     {:title (reagent/as-element [:span [ant/icon {:type "form"}] "Idea Text"])
      :type  "inner"}
     (case (:state icv)
       "LOADING"       [ant/spin {:class-name "annotated-text-container-loading spin-centered"}]
       "ANNOTATING"    [annotation-panel]
       "ALL-ANNOTATED" [annotation-panel]
       [annotation-panel-error-state])]))

(defn annotation-submit-button []
  (let [is-last-idea @(rf/subscribe [::subs/is-last-idea?])
        sync-state   @(rf/subscribe [::subs/sync-state])]
    (if is-last-idea
      [ant/button
       {:html-type "submit"
        :type      "primary"
        :size      "large"
        :block     true
        :icon      "right"
        :loading   (= sync-state :loading)
        :disabled  (not @(rf/subscribe [::icv-subs/concept-validation-submittable]))
        :on-click  #(rf/dispatch [::events/submit-concept-validation])}
       "to Survey"]
      [ant/button
       {:html-type "submit"
        :type      "primary"
        :size      "large"
        :block     true
        :icon      "right"
        :loading   (= sync-state :loading)
        :disabled  (not @(rf/subscribe [::icv-subs/concept-validation-submittable]))
        :on-click  #(rf/dispatch [::events/submit-concept-validation])}
       "Submit"])))

;;TODO loading
(defn annotator-main []
  [:div
    [ant/row
     {:gutter 20}
     [ant/col
      {:span 13}
      [:div
       [:h2.banner-header "Idea Concept Validation"]
       [:p "Please select the most appropriate concepts for the idea text below."]
       ;;TODO make configurable
       [:p
        [:small
         "The idea was generated for the challenge: "
         [:span
          "Imagine you could have a coating that could turn every surface into a touch display. Brainstorm cool products, systems, gadgets or services that could be build with it."]]]]]
     [ant/col {:span 11}
      [annotation-progress-bar]]]
    [ant/row
     {:gutter 20}
     [ant/col {:span 13}
      [icv-input-text-panel]]
     [ant/col {:span 11}
      [concept-validation-panel]]]
    [ant/row
     {:class-name "submit-button-row"}
     [ant/col
      {:span 4 :offset 10}
      [annotation-submit-button]]]])
