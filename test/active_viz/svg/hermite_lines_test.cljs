(ns active-viz.svg.hermite-lines-test
  (:require [active-viz.svg.hermite-lines :as sut]
            [active-viz.interpolation.monotone-cubic-interpolation :as int]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest hermite-lines-test
  (testing "hermite path behaves like expected"
    (let [points [[1 2] [2 1] [3 3]]
          tangents (int/interpolate points)
          path (sut/path points tangents)]

      ;; check svg path types to be included
      (is (clojure.string/includes? path "M")) ; start
      (is (clojure.string/includes? path "C")) ; first curve
      (is (clojure.string/includes? path "S")) ; follow ups

      ;; check that data points are part of the path
      (is (clojure.string/includes? path "1 2"))
      (is (clojure.string/includes? path "2 1"))
      (is (clojure.string/includes? path "3 3")))))
