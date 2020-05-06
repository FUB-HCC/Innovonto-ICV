(ns hcc.innovonto.icv.frontend.common.tracking.core
  (:require [re-frame.core :as rf]))



(rf/reg-event-db
  ::track
  (fn [db [event payload]]
    (do
      (println (str event ":" payload))
      db)))

