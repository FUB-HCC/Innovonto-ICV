(ns hcc.innovonto.icv.frontend.common.views
  (:require [antizer.reagent :as ant]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [clojure.contrib.humanize :as humanize]
            [clojure.string :as str]
            [hcc.innovonto.icv.frontend.common.subs :as subs]
            [hcc.innovonto.icv.frontend.common.events :as events]))

(def truncate-limit 240)

(defn thumbnail-or-default [thumbnail]
  (if (nil? thumbnail
        "/images/placeholder.png"
        thumbnail)))



(defn resource-candidate [resource-candidate concept-representation]
  (let [current-state (get resource-candidate :state "deselected")]
    ;;(println (str resource-candidate))
    ;;(println (str (str/upper-case concept-representation)))
    [:div.ant-list-item.resource-candidate-item
     {:class current-state
      :on-click #(if (= current-state "selected")
                  (re-frame/dispatch [::events/deselect-resource-candidate (:id resource-candidate)])
                  (re-frame/dispatch [::events/select-resource-candidate (:id resource-candidate)]))
      :key   (:id resource-candidate)}
     (if (str/includes? (str/upper-case concept-representation) "IMAGE")
       [:div.resource-image
        [:img {:src (thumbnail-or-default (:thumbnail resource-candidate))}]])
     (if (str/includes? (str/upper-case concept-representation) "DESCRIPTION")
       [:div
        [:h4 (:label resource-candidate)]
        [:p (humanize/truncate (:description resource-candidate) truncate-limit)]])]))


(defn image-grid-element [resource-candidate]
  [:div.image-grid-element
   {:class (get resource-candidate :state "deselected")
    :on-click #(if (= (get resource-candidate :state "deselected") "selected")
                (re-frame/dispatch [::events/deselect-resource-candidate (:id resource-candidate)])
                (re-frame/dispatch [::events/select-resource-candidate (:id resource-candidate)]))
    :key   (:id resource-candidate)}
   [:img {:src (thumbnail-or-default (:thumbnail resource-candidate))}]])

(defn image-grid [resource-candidates]
  [:div#resource-candidate-list.resource-candidate-list.image-grid
   (if (seq resource-candidates)
    (map image-grid-element resource-candidates)
    [:p "No resources found for the term: Please click 'nothing fits' to continue."])])

(defn resource-candidate-list [resource-candidates concept-representation]
  (if resource-candidates
    (if (= concept-representation "image")
      [image-grid resource-candidates]
      [ant/list
       {:bordered    true
        :item-layout "vertical"
        :dataSource  resource-candidates
        :render-item #(reagent/as-element [resource-candidate (js->clj %1 :keywordize-keys true) concept-representation])}])
    [:span "We couldn't find any resource candidates for the given text. Please click submit."]))

(defn- reset-scrollbar-position [element]
  (set! (.-scrollTop element) 0))

;;TODO https://stackoverflow.com/questions/8803813/how-to-reset-scrollbar-position-inside-of-a-divide-with-scrollbars
(defn select-resource-candidate-menu []
  (let [annotation-candidate @(re-frame/subscribe [::subs/current-annotation-candidate])
        concept-representation @(re-frame/subscribe [::subs/concept-representation])]
    [:div
     [:h2 (:text annotation-candidate)]
     [:div#resource-candidate-list.resource-candidate-list
      [resource-candidate-list (:resource_candidates annotation-candidate) concept-representation]]
     [:div.button-group
      [ant/button
       {:on-click #(do
                    (reset-scrollbar-position (.getElementById js/document "resource-candidate-list"))
                    (re-frame/dispatch [::events/reject-annotation-candidate (:id annotation-candidate)]))}
       "Nothing Fits"]
      [ant/button
       {:type     "primary"
        :disabled (not @(re-frame/subscribe [::subs/current-annotation-candidate-submittable]))
        :on-click #(do
                    (reset-scrollbar-position (.getElementById js/document "resource-candidate-list"))
                    (re-frame/dispatch [::events/submit-validated-resource-candidates (:id annotation-candidate)]))}
       "Accept / Continue"]]]))

(def annotation-state->color
  {"unvalidated" ""
   "validated"   "#52c41a"
   "automatically-validated" "#95de64"
   "rejected"    "#d46b08"})


(defn chunk-to-hiccup [chunk]
  (do
    ;;(println (str chunk))
    (case (:type chunk)
      "annotation" [ant/tag
                    {:class-name (if (= (:id chunk) @(re-frame/subscribe [::subs/current-annotation-candidate-index]))
                                   "ant-tag-gold"
                                   "")
                     :on-click
                     #(re-frame/dispatch [::events/select-annotation-candidate (:id chunk)])
                     :key
                     (:id chunk)
                     :color
                     (get annotation-state->color
                          @(re-frame/subscribe [::subs/annotation-validation-state (:id chunk)]))
                     :style
                     {:right-margin 0}}
                    (:text chunk)]
      "span"       [:span {:key (:id chunk)} (:text chunk)])))

(defn chunks-to-hiccup [chunks]
  [:div
   (map chunk-to-hiccup chunks)])

(defn annotated-text-panel []
  [:div
   [ant/row
    {:class-name "annotated-text-container"}
    [:div.annotated-text-textarea
     [chunks-to-hiccup @(re-frame/subscribe [::subs/chunks])]]]
   [ant/form-item
    [ant/button
     {:html-type "button"
      :type      "secondary"
      :on-click  #(re-frame/dispatch [::events/reset])}
     "Reset"]
    [ant/button
     {:html-type "submit"
      :type      "primary"
      :disabled (not @(re-frame/subscribe [::subs/concept-validation-submittable]))
      :on-click #(re-frame/dispatch [::events/submit-concept-validation])}
     "Submit"]]])
