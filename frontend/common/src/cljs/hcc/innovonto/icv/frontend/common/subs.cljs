(ns hcc.innovonto.icv.frontend.common.subs
  (:require [re-frame.core :as re-frame]))

;;SUBSCRIPTIONS
(re-frame/reg-sub
 ::chunks
 (fn [db]
   (:chunks (:icv db))))

(re-frame/reg-sub
 ::annotation-validation-state
 (fn [db [_ id]]
   (do
     (:state (nth (:annotation-candidates (:icv db)) id)))))

(re-frame/reg-sub
 ::current-annotation-candidate-index
 (fn [db]
   (do
     ;;(println
     ;;(str "Current Annotation Candidate Index = "
     ;;     (:current-annotation-candidate-index (:icv db))))
     (:current-annotation-candidate-index (:icv db)))))

(re-frame/reg-sub
 ::current-annotation-candidate
 (fn [db]
   (:current-annotation-candidate (:icv db))))

(defn at-least-one-resource-candidate-selected [resource-candidates]
  (not
    (empty?
      (filter #(= (get %1 :state "deselected") "selected") resource-candidates))))

;;The current concept is submittable if at least one concept is selected (:state selected)
(re-frame/reg-sub
 ::current-annotation-candidate-submittable
 (fn [db]
   (do
     (at-least-one-resource-candidate-selected
       (:resource_candidates (:current-annotation-candidate (:icv db)))))))

;;(first (filter #(or (= (get %1 :state "n/a") "unvalidated") (= (get %1 :state "n/a") "automatically-validated")) annotation-candidates))
(defn all-annotation-candidates-handled [annotation-candidates]
  (empty? (filter #(or (= (get %1 :state "n/a") "unvalidated") (= (get %1 :state "n/a") "automatically-validated")) annotation-candidates)))

(re-frame/reg-sub
 ::concept-validation-submittable
 (fn [db]
   (all-annotation-candidates-handled (:annotation-candidates (:icv db)))))

(re-frame/reg-sub
 ::concept-representation
 (fn [db]
   (:conceptRepresentation (:config db))))
