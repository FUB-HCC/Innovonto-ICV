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