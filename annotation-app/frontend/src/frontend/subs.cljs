(ns frontend.subs
  (:require [re-frame.core :refer [reg-sub]]))


(reg-sub
  ::active-page
  (fn [db _]
    (:active-page db)))

(reg-sub
  ::project-metadata
  (fn [db _]
    (:project-metadata db)))

(reg-sub
  ::preview-state
  (fn [db _]
    (:preview-state db)))


(reg-sub
  ::mturk-metadata
  (fn [db _]
    (:mturk-metadata db)))
