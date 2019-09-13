(ns hcc.innovonto.icv.frontend.common.events
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [hcc.innovonto.icv.frontend.common.util.trackingevents :as tracking]
            [hcc.innovonto.icv.frontend.common.config :as config]))

(re-frame/reg-event-db
 ::reset
 (fn [db _]
   (assoc db :icv
          {:state                              "INITIAL"
           :current-annotation-candidate-index 0})))


(defn to-chunks [input annotation-candidates chunks current-index]
  (if (empty? annotation-candidates)
    (if (= current-index (count input))
      chunks
      (conj chunks
            {:type "span"
             :text (subs input current-index (count input))
             :id   (str "text:" current-index "-" (count input))}))
    (let [head (first annotation-candidates)
          tail (rest annotation-candidates)]
      (if (= current-index (:offset head))
        (let [start (:offset head)
              end   (+ (:offset head) (count (:text head)))]
          ;;(println (str "Annotation Candidate - Chunk:" start " - " end))
          (to-chunks input tail (conj chunks {:type "annotation" :text (:text head) :id (:id head)}) end))
        (let [start current-index
              end   (:offset head)]
          ;;(println (str "Annotation Candidate - Span:" start " - " end))
          (to-chunks input annotation-candidates
                     (conj chunks
                           {:type "span"
                            :text (if (<= start end) (subs input start end) "*")
                            :id   (str "text:" start "-" end)})
                     end))))))

(defn add-ids [coll]
  (into [] (map-indexed #(assoc %2 :id %1) coll)))

(defn add-annotation-candidate-ids [response]
  (assoc response :annotation_candidates (add-ids (:annotation_candidates response))))

(defn add-resource-candidate-ids [response]
  (assoc response :annotation_candidates
         (map #(assoc %1 :resource_candidates (add-ids (:resource_candidates %1)))
              (:annotation_candidates response))))

;;TODO refactor. This sorts the resource candidates by confidence if specified in config, otherwise by label
(defn sort-resource-candidates-by [response sort-config]
  (case sort-config
    "confidence" (assoc response :annotation_candidates
                        (map
                         #(assoc %1 :resource_candidates
                           (into [] (sort-by :confidence > (:resource_candidates %1))))
                         (:annotation_candidates response)))
    (assoc response :annotation_candidates
           (map
            #(assoc %1 :resource_candidates (into [] (sort-by :label (:resource_candidates %1))))
            (:annotation_candidates response)))))

(defn add-state [response]
  (assoc response :annotation_candidates
         (into [] (map #(assoc %1 :state "unvalidated") (:annotation_candidates response)))))

(def auto-annotation-threshold 0.95)

;;TODO build a counter how many things we auto-annotated
(defn handle-threshold-annotation-for-resource-candidate [resource-candidate]
  (do
    (if (> (:confidence resource-candidate) auto-annotation-threshold)
      (-> resource-candidate
          (assoc :state "selected")
          (assoc :info "auto-selected"))
      resource-candidate)))

(defn state-based-on [resource-candidates auto-annotation-config]
  (if (= "semi" auto-annotation-config)
    (if (empty?
         (filter #(= (get %1 :state "unselected") "selected") resource-candidates))
      "unvalidated"
      "automatically-validated")
    (if (empty?
         (filter #(= (get %1 :state "unselected") "selected") resource-candidates))
      "unvalidated"
      "validated")))


(defn handle-threshold-annotation-for-annotation-candidate [annotation-candidate auto-annotation-config]
  (let [processed-resource-candidates (into []
                                            (map handle-threshold-annotation-for-resource-candidate
                                                 (:resource_candidates annotation-candidate)))]
    (-> annotation-candidate
        (assoc :resource_candidates processed-resource-candidates)
        (assoc :state (state-based-on processed-resource-candidates auto-annotation-config)))))


(defn handle-threshold-annotation [response auto-annotation-config]
  (assoc response :annotation_candidates
         (into []
               (map #(handle-threshold-annotation-for-annotation-candidate %1 auto-annotation-config)
                    (:annotation_candidates response)))))

(defn auto-annotate [response auto-annotation-config]
  (if (or (nil? auto-annotation-config) (= auto-annotation-config "none"))
    response
    (handle-threshold-annotation response auto-annotation-config)))

(defn sort-by-offset [response]
  (assoc response :annotation_candidates
         (sort-by :offset (:annotation_candidates response))))

(defn filter-resources-without-thumbnail [annotation-candidate]
  (assoc annotation-candidate :resource_candidates
         (into []
               (filter #(not (nil? (:thumbnail %1))) (:resource_candidates annotation-candidate)))))

(defn remove-resource-candidates-without-images [response concept-representation]
  (if (= concept-representation "image")
    (assoc response :annotation_candidates
           (map filter-resources-without-thumbnail (:annotation_candidates response)))
    response))

;;TODO can i do a nested -> here?
(defn preprocess [response config]
  (-> response
      (sort-by-offset)
      (add-annotation-candidate-ids)
      (remove-resource-candidates-without-images (:conceptRepresentation config))
      (sort-resource-candidates-by (:sort-by config))
      (add-resource-candidate-ids)
      (add-state)
      (auto-annotate (:autoAnnotation config))))

(defn response-to-chunks [response]
  (to-chunks (:text response) (:annotation_candidates response) '[] 0))

;;TODO deal with empty state.
;;TODO sync-state is a property of the overlying app: move up-to-date up
(re-frame/reg-event-fx
 ::annotation-successful
 (fn [{:keys [db]} [_ response]]
   (let [response-with-ids (preprocess response (:config db))]
     (println (str "Response: " response-with-ids))
     {:db       (-> db
                    (assoc :sync-state :up-to-date)
                    (assoc-in [:icv :input] (:text response))
                    (assoc-in [:icv :annotation-candidates] (:annotation_candidates response-with-ids))
                    (assoc-in [:icv :chunks] (response-to-chunks response-with-ids))
                    (assoc-in [:icv :state] "ANNOTATING"))
      :dispatch [::move-to-next-annotation-candidate]})))

;;TODO show error and "reset" option
(re-frame/reg-event-db
 ::annotation-failed
 (fn [db [event error]]
   (do
     (println (str event ":" error))
     (update-in db [:icv] assoc :state "error"))))

;;EVENTS
(re-frame/reg-event-fx
 ::annotate-request
 (fn [{:keys [db]} [_ input-text]]
   (do
     (println (str "Annotate Request: " input-text " - " db))
     {:db         (update-in db [:icv] assoc :state "LOADING")
      :http-xhrio {:method          :get
                   :uri             (str (:annotate config/urlconfig) "?text=" input-text "&timerValue=" (:seconds (:timer db)))
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::annotation-successful]
                   :on-failure      [::annotation-failed]}})))

(defn remove-state-from [chunks]
  (into [] (map #(dissoc %1 :state) chunks)))

(defn set-current-annotation-candidate-to [icv index]
  (-> icv
      (assoc :chunks (remove-state-from (:chunks icv)))
      (update-in [:chunks index] assoc :state "active")
      (assoc :current-annotation-candidate-index index)
      (assoc :current-annotation-candidate (nth (:annotation-candidates icv) index))))

;;TODO auto/manual
(defn next-unvalidated-annotation-candidate [annotation-candidates]
  (first (filter #(or (= (get %1 :state "n/a") "unvalidated") (= (get %1 :state "n/a") "automatically-validated")) annotation-candidates)))

;;ANNOTATION_CANDIDATES
(re-frame/reg-event-db
 ::select-annotation-candidate
 (fn [db [_ id]]
   (do
     (println (str "Selected Annotation Candidate " id))
     (assoc db :icv (set-current-annotation-candidate-to (:icv db) id)))))

(re-frame/reg-event-db
 ::move-to-next-annotation-candidate
 (fn [db _]
   (let [next-unvalidated-candidate (next-unvalidated-annotation-candidate (:annotation-candidates (:icv db)))
         id                         (:id next-unvalidated-candidate)]
     (println "move to next annotation-candidate")
     (if next-unvalidated-candidate
       (assoc db :icv (set-current-annotation-candidate-to (:icv db) id))
       (update-in db [:icv] assoc :state "ALL-ANNOTATED")))))

(re-frame/reg-event-fx
 ::submit-validated-resource-candidates
 (fn [{:keys [db]} [_ id]]
   (do
     (println "submit-validated-resource-candidates" id)
     {:db         (update-in db [:icv :annotation-candidates] assoc id (assoc (:current-annotation-candidate (:icv db)) :state "validated"))
      :http-xhrio (tracking/track
                   {:type    "icv-validate-annotation-candidate"
                    :payload {:timerValue (:seconds (:timer db))
                              :id         id
                              :text       (:text (:current-annotation-candidate (:icv db)))}}
                   ;;TODO config/urlconfig
                   )
      :dispatch   [::move-to-next-annotation-candidate]})))

(re-frame/reg-event-fx
 ::reject-annotation-candidate
 (fn [{:keys [db]} [_ id]]
   (do
     (println "reject-annotation-candidate" id)
     {:db         (update-in db [:icv :annotation-candidates] assoc id (assoc (:current-annotation-candidate (:icv db)) :state "rejected"))
      :http-xhrio (tracking/track
                   {:type    "icv-reject-annotation-candidate"
                    :payload {:timerValue (:seconds (:timer db))
                              :id         id
                              :text       (:text (:current-annotation-candidate (:icv db)))}}
                   ;;TODO config/urlconfig)
      :dispatch   [::move-to-next-annotation-candidate]})))

;; RESOURCE_CANDIDATES
(re-frame/reg-event-fx
 ::deselect-resource-candidate
 (fn [{:keys [db]} [_ id]]
   (let [resource-candidate (nth (:resource_candidates (:current-annotation-candidate (:icv db))) id)]
     (println "deselect-resource-candidate!" id)
     {:db         (update-in db [:icv :current-annotation-candidate :resource_candidates] assoc id (dissoc resource-candidate :state))
      :http-xhrio (tracking/track
                   {:type    "icv-deselect-resource-candidate"
                    :payload {:timerValue (:seconds (:timer db))
                              :id         id
                              :resource   (:resource resource-candidate)}}
                   ;;TODO config/urlconfig
                   )})))

(re-frame/reg-event-fx
 ::select-resource-candidate
 (fn [{:keys [db]} [_ id]]
   (let [resource-candidate (nth (:resource_candidates (:current-annotation-candidate (:icv db))) id)]
     (println "select-resource-candidate!" id)
     {:db         (update-in db [:icv :current-annotation-candidate :resource_candidates] assoc id (assoc resource-candidate :state "selected"))
      :http-xhrio (tracking/track
                   {:type    "icv-select-resource-candidate"
                    :payload {:timerValue (:seconds (:timer db))
                              :id         id
                              :resource   (:resource resource-candidate)}})})))

(re-frame/reg-event-db
 ::save-concept-validation-successful
 (fn [db _]
   (do
     (println "Saved ICV Result successfully.")
     db)))

;;TODO show error and "reset" option? Can I recover from this?
(re-frame/reg-event-db
 ::save-concept-validation-failed
 (fn [db _]
   (do
     (println "Saving ICV Result failed.")
     db)))

(defn is-selected? [state]
  (= state "selected"))

;;TODO refactor!!!
(defn filter-resource-candidate [resource-candidate]
  {:text       (:text resource-candidate)
   :offset     (:offset resource-candidate)
   :resource   (:resource resource-candidate)
   :source     (:source resource-candidate)
   :confidence (:confidence resource-candidate)
   :selected   (is-selected? (get resource-candidate :state "unselected"))})

(defn to-annotation [annotation-candidate]
  {:text               (:text annotation-candidate)
   :offset             (:offset annotation-candidate)
   :resourceCandidates (into [] (map filter-resource-candidate (:resource_candidates annotation-candidate)))})

(defn to-annotations [annotation-candidates]
  (into [] (map to-annotation annotation-candidates)))

(defn to-result [icv-state]
  {:text        (:input icv-state)
   :annotations (to-annotations (:annotation-candidates icv-state))})


(defn to-backend-config [config]
  {:challenge             (:challenge config)
   :conceptRepresentation (:conceptRepresentation config)
   :sortBy                (:sort-by config)
   :autoAnnotation        (:autoAnnotation config)})

;;TODO dispatch an extra event, without a handler (to be handled by the downstream app
;;TODO idea-id? TODO make idea-submit configurable?
;;TODO dispatch an idea submit with the full icv result
;;TODO add submit-concept-validation config without idea submit.
(re-frame/reg-event-fx
 ::submit-concept-validation
 (fn [{:keys [db]} _]
   (do
     (println "Submitted Concept Validation!")
     {:db         db
      :http-xhrio {:method          :post
                   :uri             (str "/hit/api/ideas/")
                   :format          (ajax/json-request-format)
                   :params          {:timerValue (:seconds (:timer db))
                                     :icvResult  (to-result (:icv db))
                                     :config     (to-backend-config (:config db))}
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::save-concept-validation-successful]
                   :on-failure      [::save-concept-validation-failed]}
      :dispatch-n [[:hcc.innovonto.icv.frontend.ideator.events/idea-form-submit-without-icv (:input (:icv db))]
                   [::reset]]})))
