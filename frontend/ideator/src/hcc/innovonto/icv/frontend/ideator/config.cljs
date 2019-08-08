(ns hcc.innovonto.icv.frontend.ideator.config)

(def debug?
  ^boolean goog.DEBUG)

;;TODO this configures the brainstorming app, the icv and the tracking-events
(def urlconfig
  (if debug?
    {:get-inspirations  "/mockdata/inspirations.json"
     :annotate          "/mockdata/annotate.json"
     :tracking-endpoint "/hit/api/event"}

    {:get-inspirations  "/hit/api/inspirations"
     :annotate          "/hit/api/annotate"
     :tracking-endpoint "/hit/api/event"}))