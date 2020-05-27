(ns frontend.survey.views
  (:require [re-frame.core :as rf]
            [frontend.subs :as subs]
            [antizer.reagent :as ant]))

(defn thank-you []
  [:h1 "Thank you!"])
