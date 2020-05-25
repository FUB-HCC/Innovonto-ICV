(ns hcc.innovonto.icv.frontend.common.tracking.core
  (:require [re-frame.core :as rf]))

(rf/reg-cofx
  :now
  (fn [coeffects _]
    (assoc coeffects :now (js.Date.))))

(rf/reg-event-fx
  ::track
  [(rf/inject-cofx :now)]
  (fn [{:keys [db now]} [_ payload]]
    {
     :db (assoc db :tracking-events (conj (:tracking-events db) (assoc payload :timestamp now)))
     }))