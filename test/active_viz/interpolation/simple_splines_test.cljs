(ns active-viz.interpolation.simple-splines-test
  (:require [active-viz.interpolation.simple-splines :as sut]
            [cljs.test :refer-macros [deftest is testing]]))

(defn pair-of-pair-of-number? [p]
  (and
    (vector? p)
    (= 2 (count p))
    (let [[[a b] [c d]] p]
      (and (number? a)
        (number? b)
        (number? c)
        (number? d)))))


(deftest simple-splines-test
  (testing "interpolation behaves like expected"
    (let [res (sut/interpolate [[1 2] [2 1] [3 3]] 0.2)]
      (is (= 3 (count res)))
      (is (every? pair-of-pair-of-number? res)))))
