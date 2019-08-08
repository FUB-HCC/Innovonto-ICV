(ns hcc.innovonto.icv.frontend.annotator.db)

;;ICV STATES: INITIAL, ANNOTATING, ALL-ANNOTATED, ERROR
(def default-db
  {:sync-state :up-to-date
   :config     {:assignmentId "testAssignment"
                :workerId     "testWorker"
                :hitId        "testHIT"
                :turkSubmitTo "http://localhost:3449/finished.html"}
   :icv        {:state                              "INITIAL"
                :current-annotation-candidate-index 0}})
