(ns frontend.survey.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::fulltext-feedback
  (fn [db _]
    (:fulltext-feedback (:survey db))))

(rf/reg-sub
  ::clarity-rating
  (fn [db _]
    (:clarity-rating (:survey db))))

(rf/reg-sub
  ::results
  (fn [db _]
    (:texts (:batch db))))

(rf/reg-sub
  ::events
  (fn [db _]
    (:events db)))