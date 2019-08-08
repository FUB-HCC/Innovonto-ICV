(ns hcc.innovonto.icv.frontend.annotator.views
  (:require [antizer.reagent :as ant]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [hcc.innovonto.icv.frontend.common.views :as icv]
            [hcc.innovonto.icv.frontend.common.subs :as icv-subs]
            [hcc.innovonto.icv.frontend.annotator.events :as events]
            [hcc.innovonto.icv.frontend.annotator.subs :as subs]
            [hcc.innovonto.icv.frontend.annotator.config :as config]))

(defn debug-panel []
  [:div
   [ant/button
    {:on-click #(re-frame/dispatch [::events/debug-print-db])}
    "Print DB"]])

(defn annotation-progress-bar []
  (let [handled-ideas   @(re-frame/subscribe [::subs/number-handled])
        unhandled-ideas @(re-frame/subscribe [::subs/number-unhandled])
        percentage-done @(re-frame/subscribe [::subs/percentage-done])]
    [:div
     [:p "Progress"]
     [ant/progress {:percent percentage-done}]
     [:p
      (str "You have annotated " handled-ideas " Ideas. Ideas left: " unhandled-ideas)]]))

;;TODO states: empty, loading, some, error
(defn concept-validation-panel []
  (let [component-state (:state @(re-frame/subscribe [::subs/icv]))]
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
     [icv/chunks-to-hiccup @(re-frame/subscribe [::icv-subs/chunks])]]]])

(defn annotation-panel-error-state []
  (let [current-idea-index @(re-frame/subscribe [::subs/current-idea-index])]
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
        :on-click  #(re-frame/dispatch [::events/load-icv-for-idea current-idea-index])}
       "Reset"]]]))

(defn icv-input-text-panel []
  (let [icv @(re-frame/subscribe [::subs/icv])]
    [ant/card
     {:title (reagent/as-element [:span [ant/icon {:type "form"}] "Idea Text"])
      :type  "inner"}
     (case (:state icv)
       "LOADING"       [ant/spin {:class-name "annotated-text-container-loading spin-centered"}]
       "ANNOTATING"    [annotation-panel]
       "ALL-ANNOTATED" [annotation-panel]
       [annotation-panel-error-state])]))

(defn annotation-submit-button []
  (let [is-last-idea @(re-frame/subscribe [::subs/is-last-idea?])
        sync-state   @(re-frame/subscribe [::subs/sync-state])]
    (if is-last-idea
      [ant/button
       {:html-type "submit"
        :type      "primary"
        :size      "large"
        :block     true
        :icon      "right"
        :loading   (= sync-state :loading)
        :disabled  (not @(re-frame/subscribe [::icv-subs/concept-validation-submittable]))
        :on-click  #(re-frame/dispatch [::events/submit-concept-validation])}
       "to Survey"]
      [ant/button
       {:html-type "submit"
        :type      "primary"
        :size      "large"
        :block     true
        :icon      "right"
        :loading   (= sync-state :loading)
        :disabled  (not @(re-frame/subscribe [::icv-subs/concept-validation-submittable]))
        :on-click  #(re-frame/dispatch [::events/submit-concept-validation])}
       "Submit"])))

;;TODO loading
(defn annotator-main []
  [ant/layout
   (if config/debug?
     [debug-panel])
   [ant/layout-content
    [ant/row
     {:gutter 20}
     [ant/col
      {:span 13}
      [:div
       [:h2.banner-header "Idea Concept Validation"]
       [:p "Please select the most appropiate concepts for the idea text below."]
       ;;TODO make configurable
       [:p
        [:small
         "The ideas were generated for the challenge: "
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
      [annotation-submit-button]]]
    [ant/row
     [:p.footer
      "Provided by the HCC @ FU Berlin."
      [:br]
      "If you have questions please contact us."
      [:br]
      "  If you can not finish this hit due to errors, please contact us via the mturk interface."]]]])

