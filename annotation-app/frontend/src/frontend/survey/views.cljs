(ns frontend.survey.views
  (:require [re-frame.core :as rf]
            [frontend.subs :as subs]
            [frontend.survey.subs :as survey-subs]
            [frontend.survey.events :as events]
            [antizer.reagent :as ant]))

(def project-homepage "https://www.mi.fu-berlin.de/en/inf/groups/hcc/research/projects/innovonto/index.html")

(defn prevent-submit-and-dispatch [event]
  (do
    (rf/dispatch [::events/annotation-batch-submit])
    (.preventDefault event)
    false))

(defn clj->json
  [ds]
  (.stringify js/JSON (clj->js ds)))

(defn thank-you []
  (let [mturk-metadata @(rf/subscribe [::subs/mturk-metadata])
        results @(rf/subscribe [::survey-subs/results])
        tracking-events @(rf/subscribe [::survey-subs/tracking-events])]
    [:div
     [:h1 "Thank you!"]
     [:div
      [:p "Thank you for your participation! If you want to know more about the project" [:a {:href project-homepage :target "_blank"} " click here."]]]
     [:form#mturkForm {:method "POST" :action (:turk-submit-to mturk-metadata) :on-submit #(prevent-submit-and-dispatch %1)}
      [:input {:type "hidden" :name "hitId" :value (:hit-id mturk-metadata)}]
      [:input {:type "hidden" :name "workerId" :value (:worker-id mturk-metadata)}]
      [:input {:type "hidden" :name "assignmentId" :value (:assignment-id mturk-metadata)}]
      [:input {:type "hidden" :name "projectId" :value (:project-id mturk-metadata)}]
      [:input {:type "hidden" :name "clarity-rating" :value @(rf/subscribe [::survey-subs/clarity-rating])}]
      [:input {:type "hidden" :name "results" :value (clj->json results)}]
      [:input {:type "hidden" :name "tracking-events" :value (clj->json tracking-events)}]
      ;TODO required
      [ant/form-item
       [:p "How clear was the HIT?"]
       [ant/row
        [ant/col {:span 3 :class "centered"} [:span "Very Unclear"]]
        [ant/col {:span 10}
         [ant/slider {:min       -2 :max 2
                      :value     @(rf/subscribe [::survey-subs/clarity-rating])
                      :on-change #(rf/dispatch [::events/set-clarity-rating %])}]]
        [ant/col {:span 3 :class "centered"} [:span "Very Clear"]]]]
      [ant/form-item
       [:p "We try to provide the best possible user experience, so it's always valuable to get feedback. If you
                    have some
                    advice or recommendations for us, please put it here:"]
       [ant/input-text-area {:name        "fulltext-feedback"
                             :cols        "3"
                             :rows        "3"
                             :placeholder "Feedback"
                             :value       @(rf/subscribe [::survey-subs/fulltext-feedback])
                             :on-change   #(rf/dispatch [::events/set-fulltext-feedback (-> % .-target .-value)])}]]
      [ant/button {:html-type "submit" :type "primary" :size "large"} "Submit!"]]]))
