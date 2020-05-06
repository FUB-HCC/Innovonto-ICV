(ns frontend.views
  (:require [re-frame.core :as rf]
            [frontend.subs :as subs]
            [antizer.reagent :as ant]
            [frontend.intro.views :as intro-views]
            [frontend.annotator.views :as annotator-views]))

(defn footer []
  [ant/row
   [:hr]
   [:p.footer
    "Provided by the HCC Group @ FU Berlin."
    [:br]
    "If you have any questions please contact us."
    [:br]
    "  If you can not finish this hit due to errors, please contact us via the mturk interface."]])

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
    :home [intro-views/intro]
    :tutorial [tutorial]
    :annotator [annotator-views/annotator-main]
    :thank-you [thank-you]
    [intro-views/intro]))

(defn annotation-app
  []
  (let [active-page @(rf/subscribe [::subs/active-page])]
    [ant/layout
     [ant/layout-content
      [pages active-page]
      [footer]
      ]]))