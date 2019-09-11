(defproject de.active-group/active-viz "0.2.6-SNAPSHOT"
  :description "Active Viz: ClojureScript SVG visualization tools"
  :url "https://github.com/active-group/active-viz"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [de.active-group/active-clojure "0.31.0"]]

  :source-paths ["src"]
  :aliases {"fig:test"        ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" active-viz.test-runner]
            "fig:test-travis" ["trampoline" "run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-fwo" "{:launch-js [\"google-chrome-stable\" \"--no-sandbox\" \"--headless\" \"--disable-gpu\" \"--repl\" :open-url] :repl-eval-timeout 30000}"  "-m" active-viz.test-runner]
            }
  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.1.9"]]}})
