(ns frontend.config)

; TODO remove or integrate in url config.
(def debug?
  ^boolean goog.DEBUG)

(def base-URL "http://localhost:9002/api")

(def urlconfig
  {:annotate         (str base-URL "/annotate")
   :project-metadata (str base-URL "/mturk/projectMetadata")
   :annotation-batch (str base-URL "/mturk/annotation/batch")
   :submit           (str base-URL "/mturk/annotation/submit")})


