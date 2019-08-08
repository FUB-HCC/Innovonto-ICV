(defproject hcc.innovonto.icv.frontend/common "0.1.0-SNAPSHOT"
  :description "The commons library for interactive concept validation. Defines components used in both projects."
  :url "https://github.com/FUB-HCC/Innovonto-ICV"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [clojure-humanize "0.2.2"]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [re-frame "0.10.5"]
                 [antizer "0.3.1"]
                 [day8.re-frame/http-fx "0.1.6"]]

  :plugins [[lein-cljsbuild "1.1.7"]]
  :hooks [leiningen.cljsbuild]

  :cljsbuild {:builds
              [
               {
                :id "dev"
                :source-paths ["src/cljs"]
                :jar true
                :compiler {:output-to "resources/public/js/common.js"
                           :optimizations :whitespace
                           :pretty-print true}}]})
