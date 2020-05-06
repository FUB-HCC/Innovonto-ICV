(defproject frontend "0.1.0-SNAPSHOT"
  :description "A app to annotate idea texts with semantic concepts."
  :url "https://github.com/FUB-HCC/Innovonto-ICV"
  :license {:name "GNU Affero General Public License v3.0"
            :url "https://www.gnu.org/licenses/agpl-3.0.en.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.758"]
                 [reagent "0.10.0"]
                 [re-frame "0.12.0"]
                 [day8.re-frame/http-fx "v0.2.0"]
                 [simplect/antizer "3.26.12-1"]
                 [secretary "1.2.3"]]

  :source-paths ["src"]

  ;;TODO clean
  :aliases {"fig"       ["run" "-m" "figwheel.main"]
            "fig:build" ["run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "frontend.test-runner"]}

  ;;TODO devtools
  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.4"]]
                   }})

