;; This test runner is intended to be run from the command line
(ns active-viz.test-runner
  (:require
    ;; require all the namespaces that you want to test
    [active-viz.scale.util-test]
    [active-viz.scale.linear-scale-test]
    [active-viz.scale.time-scale-test]
    [active-viz.ticks-test]
    [active-viz.interpolation.monotone-cubic-interpolation-test]
    [active-viz.interpolation.simple-splines-test]
    [active-viz.svg.hermite-lines-test]
    [active-viz.svg.splines-test]
    [figwheel.main.testing :refer [run-tests-async]]))

(defn -main [& args]
  (run-tests-async 5000))
