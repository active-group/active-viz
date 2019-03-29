(ns active-viz.interpolation.monotone-cubic-interpolation-test
  (:require [active-viz.interpolation.monotone-cubic-interpolation :as sut]
            [cljs.test :refer-macros [deftest is testing]]))

(defn pair-of-number? [p]
  (and
    (vector? p)
    (= 2 (count p))
    (number? (first p))
    (number? (second p))))


(deftest interpolation-test
  (testing "interpolate behaves like expected"
    (let [res (sut/interpolate [[1 2] [2 1] [3 3]])]
      (is (= 3 (count res)))
      (is (every? pair-of-number? res)))))
