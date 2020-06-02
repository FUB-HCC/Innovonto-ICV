(ns hcc.innovonto.icv.frontend.annotator.subs
  (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
  ::icv
  (fn [db]
    (:icv db)))


(defn handled-texts [texts]
  (filter #(= (get %1 :state "unhandled") "handled") texts))

(defn unhandled-texts [texts]
  (filter #(= (get %1 :state "unhandled") "unhandled") texts))

(re-frame/reg-sub
  ::all-ideas-handled?
  (fn [db]
    (empty? (unhandled-texts (:texts (:batch db))))))

(re-frame/reg-sub
  ::is-last-idea?
  (fn [db]
    (let [current-idea-index (:current-idea-index (:batch db))
          ideas (:texts (:batch db))]
      (= current-idea-index (- (count ideas) 1)))))

(re-frame/reg-sub
  ::number-handled
  (fn [db]
    (count (handled-texts (:texts (:batch db))))))

(re-frame/reg-sub
  ::number-unhandled
  (fn [db]
    (count (unhandled-texts (:texts (:batch db))))))

(re-frame/reg-sub
  ::percentage-done
  (fn [db]
    (let [texts (:texts (:batch db))
          handled (handled-texts texts)]
      (if (= (count handled) 0)
        0
        (* (/ (count handled) (count texts)) 100)))))

;;TODO implement sync-state depending on submodules
(re-frame/reg-sub
  ::sync-state
  (fn [db]
    (:sync-state db)))

(re-frame/reg-sub
  ::current-challenge
  (fn [db]
    (let [current-challenge-name (get-in db [:batch :texts (:current-idea-index (:batch db)) :hasIdeaContest])]
      (get-in db [:batch :challenges (keyword current-challenge-name)]))))

(re-frame/reg-sub
  ::current-idea-index
  (fn [db]
    (:current-idea-index (:batch db))))