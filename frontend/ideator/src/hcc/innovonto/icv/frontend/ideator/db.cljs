(ns hcc.innovonto.icv.frontend.ideator.db)

;;ICV STATES: INITIAL, ANNOTATING, ALL-ANNOTATED, ERROR
(def default-db
  {
    :sync-state :up-to-date
    :challenge-config {
                        :task "Your task is to come up with as many ideas as you can to address the problem below. Be as specific as possible in your responses. If you have any issues with the system, try refreshing the page: it will maintain your ideas and the timer in the same place as before."
                        :challenge "Imagine you could have a coating that could turn every surface into a touch display. Brainstorm cool products, systems, gadgets or services that could be build with it."}
    :timer {
             :total-seconds (* 60 10)
             :seconds (* 60 10)
             :state "stopped"}
    :submitted-ideas '()
    :config {
              :assignmentId "testAssignment"
              :workerId "testWorker"
              :hitId "testHIT"
              :turkSubmitTo "http://localhost:3449/finished.html"}
    :icv {
           :state "INITIAL"
           :current-annotation-candidate-index 0}
    :inspirations {
                    :state "INITIAL"
                    }})
