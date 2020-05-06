(ns frontend.events-test
  (:require
    [cljs.test :refer-macros [deftest is testing]]
    [frontend.events :as events]))

(deftest preview
  (is (= :preview (events/determine-preview-state {:project-id "testproject" :hit-id "testhit" :assignment-id "ASSIGNMENT_ID_NOT_AVAILABLE"}))))

(deftest start
  (is (= :start (events/determine-preview-state {:project-id       "testproject"
                                                   :hit-id         "testhit"
                                                   :worker-id      "testworker"
                                                   :assignment-id  "assignment-id"
                                                   :turk-submit-to "http://localhost:9500"}))))

(deftest invalid
  (is (= :invalid (events/determine-preview-state {}))))
