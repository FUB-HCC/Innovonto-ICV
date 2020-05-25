(ns hcc.innovonto.icv.frontend.common.spec
  (:require [clojure.spec.alpha :as s]))


(defn zero-or-positive? [number]
  (or (zero? number) (pos? number)))

;;Resource Candidates:
(s/def ::description string?)
(s/def ::offset (s/and int? zero-or-positive?))
(s/def ::source string?)
(s/def ::thumbnail string?)                                 ;TODO url?
;;Token span
(s/def ::start (s/and int? zero-or-positive?))
(s/def ::end (s/and int? zero-or-positive?))
(s/def ::token_span (s/keys :req-un [::start ::end]))
(s/def ::label string?)
(s/def ::id (s/and int? zero-or-positive?))
(s/def ::resource string?)                                  ;TODO url?
(s/def ::confidence number?)
(s/def ::text string?)

(s/def ::resource-candidate (s/keys :req-un [::description ::offset ::source ::thumbnail ::token_span ::label ::id ::resource ::confidence ::text]))

(s/explain ::resource-candidate {:description
                                             "A fortepiano [╦îf╔örte╦êpja╦Éno] is an early piano. In principle, the word \"fortepiano\" can designate any piano dating from the invention of the instrument by Bartolomeo Cristofori around 1700 up to the early 19th century.
                                       Most typically, however, it is used to refer to the late-18th to early-19th century instruments for which Haydn, Mozart, and the younger Beethoven wrote their piano music. Starting in Beethoven's time, the fortepiano began a period
                                       of steady evolution, culminating in the late 19th century with the modern grand. The earlier fortepiano became obsolete and was absent from the musical scene for many decades. In the 20th century the fortepiano was revived, followin
                                       g the rise of interest in historically informed performance. Fortepianos are built for this purpose today in specialist workshops.",
                                 :offset     11,
                                 :source     "babelfy",
                                 :thumbnail
                                             "http://commons.wikimedia.org/wiki/Special:FilePath/FortepianoByMcNultyAfterWalter1805.jpg?width=300",
                                 :token_span {:end 2, :start 2},
                                 :label      "Fortepiano",
                                 :id         0,
                                 :resource   "http://dbpedia.org/resource/Fortepiano",
                                 :confidence 0,
                                 :text       "piano"})


(s/def ::state string?)
(s/def ::resource_candidates (s/* ::resource-candidate))
(s/def ::annotation-candidate (s/keys :req-un [::offset ::resource_candidates ::text ::token_span ::id ::state]))

(s/explain ::annotation-candidate {:offset     11,
                                   :resource_candidates
                                               [{:description
                                                             "A fortepiano [╦îf╔örte╦êpja╦Éno] is an early piano. In principle, the word \"fortepiano\" can designate any piano dating from the invention of the instrument by Bartolomeo Cristofori around 1700 up to the early 19th century.
                                                       Most typically, however, it is used to refer to the late-18th to early-19th century instruments for which Haydn, Mozart, and the younger Beethoven wrote their piano music. Starting in Beethoven's time, the fortepiano began a period
                                                       of steady evolution, culminating in the late 19th century with the modern grand. The earlier fortepiano became obsolete and was absent from the musical scene for many decades. In the 20th century the fortepiano was revived, followin
                                                       g the rise of interest in historically informed performance. Fortepianos are built for this purpose today in specialist workshops.",
                                                 :offset     11,
                                                 :source     "babelfy",
                                                 :thumbnail
                                                             "http://commons.wikimedia.org/wiki/Special:FilePath/FortepianoByMcNultyAfterWalter1805.jpg?width=300",
                                                 :token_span {:end 2, :start 2},
                                                 :label      "Fortepiano",
                                                 :id         0,
                                                 :resource   "http://dbpedia.org/resource/Fortepiano",
                                                 :confidence 0,
                                                 :text       "piano"}],
                                   :text       "piano",
                                   :token_span {:end 2, :start 2},
                                   :id         0,
                                   :state      "unvalidated"})