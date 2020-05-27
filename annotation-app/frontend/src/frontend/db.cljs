(ns frontend.db)

;;ICV STATES: INITIAL, ANNOTATING, ALL-ANNOTATED, ERROR
(def default-db {
                 :sync-state       :up-to-date
                 :active-page      :home
                 :icv              {:state                              "INITIAL"
                                    :current-annotation-candidate-index 0}
                 :tracking-events   []
                 :annotator-config {
                                    :concept-representation "image-and-description"
                                    :sort-by                "confidence"
                                    :auto-annotation        "none"
                                    }
                 :batch            {
                                    :current-idea-index 0
                                    :texts              [{:id "38aa2640-6efb-49ee-afd7-fb7a786cb406" :text "A window where you can control how much light gets in by swiping."}
                                                         {:id "54764cfe-83f4-4a16-b9a0-2294019522dc" :text "A portable piano"}]
                                    }})