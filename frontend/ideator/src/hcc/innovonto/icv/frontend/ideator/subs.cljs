(ns hcc.innovonto.icv.frontend.ideator.subs
  (:require
    [re-frame.core :as re-frame]))

;;TODO move into reframetimer.cljs
(re-frame/reg-sub
 ::timer
 (fn [db]
   (:timer db)))

(re-frame/reg-sub
 ::modal-visible
 (fn [db]
   (:modal db)))

(re-frame/reg-sub
 ::challenge
 (fn [db]
   (:challenge (:challenge-config db))))

(re-frame/reg-sub
 ::submitted-ideas
 (fn [db]
   (:submitted-ideas db)))

(re-frame/reg-sub
 ::inspirations
 (fn [db]
   (:inspirations db)))

(re-frame/reg-sub
 ::icv
 (fn [db]
   (:icv db)))
