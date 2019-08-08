(ns hcc.innovonto.icv.frontend.ideator.views
  (:require
    [antizer.reagent :as ant]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [hcc.innovonto.icv.frontend.common.util.timer :as timer]
    [hcc.innovonto.icv.frontend.common.views :as icv]
    [hcc.innovonto.icv.frontend.ideator.config :as config]
    [hcc.innovonto.icv.frontend.ideator.events :as events]
    [hcc.innovonto.icv.frontend.ideator.subs :as subs]))

(defn time-is-up-modal [modal-sub]
  [ant/modal {:title (reagent/as-element [:span [ant/icon {:type "check-circle" :style {:color "#52c41a"}}] "Time is up"])
              :visible (:show? @modal-sub)
              :closable false
              :mask-closable false
              :footer (reagent/as-element [ant/button {:href "survey.html" :type "primary"} "Continue to survey"])}
   "Thank you very much for your participation!"])

(defn debug-panel []
  [:div
   [ant/button {:on-click #(re-frame/dispatch [:hcc.innovonto.icv.frontend.common.util.reframetimer/start-timer])} "Start Timer"]
   [ant/button {:on-click #(re-frame/dispatch [:hcc.innovonto.icv.frontend.common.util.reframetimer/stop-timer])}  "Stop Timer"]
   [ant/button {:on-click #(re-frame/dispatch [:hcc.innovonto.icv.frontend.common.util.reframetimer/debug-reset-timer])} "Reset Timer"]
   [ant/button {:on-click #(re-frame/dispatch [::events/debug-print-db])} "Print DB"]
   [ant/button {:on-click #(re-frame/dispatch [::events/debug-show-modal])} "Show Modal"]])

;;TODO this has to be scrollable!
(defn idea-history []
  (let [submitted-ideas (re-frame/subscribe [::subs/submitted-ideas])]
    [ant/card {:title (reagent/as-element [:span [ant/icon {:type "clock-circle-o"}] "Your previous ideas"])
               :type "inner"
               :class-name "clickable-card"}
     [:div.idea-history
      [ant/list {:bordered true
                 :item-layout "vertical"
                 :dataSource (if @submitted-ideas @submitted-ideas [])
                 :render-item #(reagent/as-element [ant/list-item (get (js->clj %1) "text")])}]]]))

(defn task-description []
  [ant/alert {
               :message "Your task is to come up with as many ideas as you can to address the problem below. Be as specific as possible in your responses. If you have any issues with the system, try refreshing the page: it will maintain your ideas and the timer in the same place as before."
               :type "warning"
               :show-icon true
               :banner true}])

;;TODO reset form fields after successful submit? (after ajax call came back successful)
;;TODO reset-fields my-form doesn't work!
(defn submit-form [my-form errors values]
  (if (empty? (js->clj errors))
    (do
      (re-frame/dispatch [::events/idea-form-submit (get (js->clj values) "description")])
      (ant/reset-fields my-form))))

;;TODO on-enter behavior is not nice yet.
(defn submit-new-idea-form []
  (fn [props]
    (let [my-form (ant/get-form)]
      [ant/form {:layout "vertical" :on-submit #(.preventDefault %1)}
       [ant/form-item {:label "Description"}
        (ant/decorate-field my-form "description" {:rules [{:required true :message "Please provide a description."}]}
                            [ant/input-text-area {:rows 8 :placeholder "Type your idea in here. Be very specific, and write as many details as possible! Only one idea at a time." :on-press-enter #(ant/validate-fields my-form (partial submit-form my-form))}])]
       [ant/form-item
        [ant/button {:html-type "button" :type "secondary" :on-click #(ant/reset-fields my-form)} "Reset"]
        [ant/button {:html-type "submit" :type "primary" :on-click #(ant/validate-fields my-form (partial submit-form my-form))} "Submit"]
        ]])))

(defn submit-idea-panel-error-state []
  [:div
   [ant/alert {:message "There was an error on the server! Please reset the panel and try again." :type "error"}]
   [ant/row
    [:div.annotated-text-textarea
     [:p " "]]]
   [ant/form-item
    [ant/button
     {:html-type "button"
      :type      "secondary"
      :on-click  #(re-frame/dispatch [:hcc.innovonto.icv.frontend.common.events/reset])}
     "Reset"]
    [ant/button
     {:html-type "submit"
      :type      "primary"
      :disabled true} "Submit"]]])


;;TODO loading state?
(defn submit-idea-panel []
  (let [icv @(re-frame/subscribe [::subs/icv])]
    [ant/card {:title (reagent/as-element [:span [ant/icon {:type "form"}] "Submit a new idea"])
               :type "inner"}
     (case (:state icv)
       "INITIAL" (ant/create-form (submit-new-idea-form))
       "LOADING" [ant/spin {:class-name "annotated-text-container-loading spin-centered"}]
       "ANNOTATING" [icv/annotated-text-panel]
       "ALL-ANNOTATED" [icv/annotated-text-panel]
       [submit-idea-panel-error-state])]))


;;TODO states: empty, loading, some, error
(defn concept-validation-panel []
  (let [component-state (:state @(re-frame/subscribe [::subs/icv]))]
    [ant/card {:title (reagent/as-element [:span [ant/icon {:type "check-circle-o"}] "Validation"])
               :type "inner"}
     (case component-state
       "INITIAL" [:div "Please Submit an Idea first."]
       "LOADING" [ant/spin {:class-name "spin-centered"}]
       "ANNOTATING" [icv/select-resource-candidate-menu]
       "ALL-ANNOTATED" [:div "Everything is annotated. Please click submit"]
       [:p "There was an error on the server! Please reset the panel and try again."])]))

(defn load-inspirations-button []
  [:div {:on-click #(re-frame/dispatch [::events/request-random-inspiration])}
   [ant/alert {:message "Need Inspiration? Click here!"
               :description "You will be presented with a set of others' ideas. Feel free to use them as inspiration: remix them with your own ideas, expand on them, or use them in any way you'd like!"
               :type "info"
               :style {:cursor "pointer"}
               :show-icon true}]])

(defn inspiration-panel-error-state []
  [:div
   [ant/alert {:type "error" :message "There was an error loading the inspirations. Please try again:"}]
   [load-inspirations-button]])

(defn inspiration-panel []
  (let [inspirations (re-frame/subscribe [::subs/inspirations])
        component-state (:state @inspirations)]
    [ant/card {:title (reagent/as-element [:span [ant/icon {:type "bulb"}] "Inspiration"])
               :type "inner"}
      (case component-state
        "INITIAL" [:div
                   [load-inspirations-button]
                   (if (:ideas @inspirations)
                     [ant/list {:bordered true
                                :item-layout "vertical"
                                :dataSource (:ideas @inspirations)
                                :render-item #(reagent/as-element [ant/list-item (get (js->clj %1) "text")])}])]
        "LOADING" [ant/spin {:class-name "spin-centered"}]
        [inspiration-panel-error-state])]))

;;TODO re-order? (Last submitted idea -> top?)
(defn idea-history-panel []
  (let [submitted-ideas (re-frame/subscribe [::subs/submitted-ideas])]
    [ant/card {:title (reagent/as-element [:span [ant/icon {:type "clock-circle-o"}] "Your previous ideas"])
               :type "inner"}
     [:div.idea-history
      [ant/list {:bordered true
                 :item-layout "vertical"
                 :dataSource (if @submitted-ideas @submitted-ideas [])
                 :render-item #(reagent/as-element [ant/list-item (get (js->clj %1) "text")])}]]]))

;;TODO global loading state?
;;TODO global notifications: neded?
(defn brainstorming-session-main []
  (let [timer-sub (re-frame/subscribe [::subs/timer])
        modal-sub (re-frame/subscribe [::subs/modal-visible])
        challenge (re-frame/subscribe [::subs/challenge])]
    [ant/layout
     (if config/debug?
       [debug-panel])
     [task-description]
     ;;TODO loading
     [ant/layout-content
      [ant/row
       [ant/col {:span 4}
        [timer/timer-component timer-sub]]
       [ant/col {:span 10 :class-name "challenge-header"}
        [:h2 "Challenge"]
        [:p @challenge]]]
      [ant/row {:gutter 20}
       [ant/col {:span 13}
        [submit-idea-panel]]
       [ant/col {:span 11}
        [concept-validation-panel]]]
      [ant/row {:gutter 20}
        [ant/col {:span 13}
         [inspiration-panel]]
        [ant/col {:span 11}
         [idea-history-panel]]]
      [ant/row
       [:p.footer
        "Provided by the HCC @ FU Berlin."
        [:br]
        "If you have questions please contact us."
        [:br]
        "  If you can not finish this hit due to errors, please contact us via the mturk interface."]]]
     [time-is-up-modal modal-sub]]))
