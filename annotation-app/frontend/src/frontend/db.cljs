(ns frontend.db)

;;ICV STATES: INITIAL, ANNOTATING, ALL-ANNOTATED, ERROR
(def default-db {
                 :sync-state       :up-to-date
                 :last-error       ""
                 :active-page      :home
                 :icv              {:state                              "INITIAL"
                                    :current-annotation-candidate-index 0}
                 :tracking-events  []
                 :annotator-config {
                                    :concept-representation "image-and-description"
                                    :sort-by                "confidence"
                                    :auto-annotation        "none"
                                    }
                 :batch            {}
                 :survey           {
                                    :clarity-rating 0
                                    }})