(ns hcc.innovonto.icv.frontend.annotator.config)

(def debug?
  ^boolean goog.DEBUG)

;;TODO this configures the brainstorming app, the icv and the tracking-events
(def urlconfig
  (if debug?
    {:annotate          "/mockdata/annotate.json"
     :submit            "/mockdata/submit.json"
     :tracking-endpoint "/hit/api/event"}

    {:annotate          "/hit/api/annotate"
     :submit            "/hit/api/annotator/submit"
     :tracking-endpoint "/hit/api/event"}))