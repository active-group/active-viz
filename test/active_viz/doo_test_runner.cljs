(ns active-viz.doo-test-runner
  (:require [cljs.test :as t :include-macros true]
            [active-viz.scale-test]
            [active-viz.scale.linear-scale-test]
            [active-viz.scale.time-scale-test]
            [active-viz.ticks-test]
            [active-viz.interpolation.monotone-cubic-interpolation-test]
            [active-viz.interpolation.simple-splines-test]
            [active-viz.svg.hermite-lines-test]
            [active-viz.svg.splines-test]
            [doo.runner :refer-macros [doo-tests]]))

(doo-tests
  'active-viz.scale-test
  'active-viz.scale.linear-scale-test
  'active-viz.scale.time-scale-test
  'active-viz.ticks-test
  'active-viz.interpolation.monotone-cubic-interpolation-test
  'active-viz.interpolation.simple-splines-test
  'active-viz.svg.hermite-lines-test
  'active-viz.svg.splines-test)
