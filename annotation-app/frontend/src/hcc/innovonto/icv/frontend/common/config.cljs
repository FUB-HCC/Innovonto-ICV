(ns hcc.innovonto.icv.frontend.common.config)

(def debug?
  ^boolean goog.DEBUG)

(def urlconfig
  (if debug?
    {:get-inspirations  "/mockdata/inspirations.json"
     :annotate          "/mockdata/annotate.json"
     :tracking-endpoint "/hit/api/event"}

    {:get-inspirations  "/hit/api/inspirations"
     :annotate          "/hit/api/annotate"
     :tracking-endpoint "/hit/api/event"}))
