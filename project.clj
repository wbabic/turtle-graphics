(defproject turtle-graphics "0.1.1"
  :description "Turtle Graphics in clojurescript"
  :url "http://wbabic.github.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/tools.reader "1.0.0-alpha1"]
                 [org.clojure/tools.analyzer.jvm "0.6.9"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.async "0.2.374"]

                 [devcards "0.2.1-4"]
                 [figwheel-sidecar "0.5.0-3"
                  :exclusions [clj-time
                               joda-time
                               org.clojure/tools.reader]
                  :scope "test"]
                 [sablono "0.5.3"]
                 [org.omcljs/om "0.9.0"]
                 [reagent "0.5.1"]

                 [thi.ng/geom "0.0.908" :exclusions [org.clojure/tools.analyzer.jvm]]
                 [thi.ng/color "1.0.0"]
                 [thi.ng/strf "0.2.1"]

                 [complex/complex "0.1.9"]

                 ;; for conflict resolution
                 [clj-time "0.9.0"]
                 [ring/ring-core "1.4.0"]]

  :source-paths ["src"]

  :plugins [[lein-cljsbuild "1.1.2"]]

  :clean-targets ^{:protect false} ["resources/public/cards" "target"]

  :figwheel {:build-ids ["cards"]
             :css-dirs ["resources/public/css"]}

  :cljsbuild {
              :builds
              [
               {:id           "cards"
                :figwheel     {:devcards true}
                :source-paths ["src"]
                :compiler     {:main       turtle-graphics.devcards
                               :source-map-timestamp true
                               :asset-path "cards"
                               :output-to  "resources/public/cards/main.js"
                               :output-dir "resources/public/cards"}}
               ]})
