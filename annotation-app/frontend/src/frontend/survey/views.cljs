(ns frontend.survey.views
  (:require [re-frame.core :as rf]
            [frontend.subs :as subs]
            [frontend.survey.events :as events]
            [antizer.reagent :as ant]))

(def project-homepage "https://www.mi.fu-berlin.de/en/inf/groups/hcc/research/projects/innovonto/index.html")

(defn prevent-submit-and-dispatch [event]
  (do
    (rf/dispatch [::events/annotation-batch-submit])
    (.preventDefault event)
    false))

;;TODO on submit!?
;;TODO check that mturk-metadata has values. If not, try to get them from somewhere.
(defn thank-you []
  (let [mturk-metadata @(rf/subscribe [::subs/mturk-metadata])]
    [:div
     [:h1 "Thank you!"]
     [:form#mturkForm {:method "POST" :action (:turk-submit-to mturk-metadata) :on-submit #(prevent-submit-and-dispatch %1)}
      [:input {:type "hidden" :name "assignmentId" :value (:assignment-id mturk-metadata)}]
      [:input {:type "Submit" :value "Submit!"}]
      [:div
       [:p "Thank you for your participation! If you want to know more about the project" [:a {:href project-homepage :target "_blank"} " click here."]]]]]))

; TODO data that needs to be submitted:
; Mturk metadata: HWAP
; Annotation results
; Tracking events

;  complexSubmit (): void {
;    vxm.ideaModule.submitRatingData().then(() => {
;      this.submittedToOwnServer = true
;    })
;  }

