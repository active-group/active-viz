(ns active-viz.svg.splines-test
  (:require [active-viz.svg.splines :as sut]
            [active-viz.interpolation.simple-splines :as int]
            [cljs.test :refer-macros [deftest is testing]]))



(deftest splines-test
  (testing "splines path behaves like expected"
    (let [points         [[1 2] [2 1] [3 3]]
          control-points (int/interpolate points 0.2)
          path           (sut/path points control-points)]

      ;; check svg path types to be included
      (is (clojure.string/includes? path "M")) ; start
      (is (clojure.string/includes? path "C")) ; curves

      ;; check that data points are part of the path
      (is (clojure.string/includes? path "1 2"))
      (is (clojure.string/includes? path "2 1"))
      (is (clojure.string/includes? path "3 3")))))

