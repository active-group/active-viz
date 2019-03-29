(ns active-viz.ticks-test
  (:require [active-viz.ticks :as ticks]
            [cljs.test :refer-macros [deftest is testing]]))

(deftest ticks-test
  (testing "generates ticks based on prefered amount"
    ;; Testing with m=4 here since upper bound is 3*4 which results in 11 ticks
    (is (= {:lstep 1 :lmin 0 :lmax 10} (dissoc (ticks/nice-ticks 0 10 4) :score)))
    (is (= {:lstep 2.5 :lmin 0 :lmax 10} (dissoc (ticks/nice-ticks 0 10 2) :score))))

  (testing "generates ticks based on passed nice vectors"
    (is (= {:lstep 2 :lmin 0 :lmax 12} (dissoc (ticks/nice-ticks 0 10 4 [2 4]) :score)))
    (is (= {:lstep 4 :lmin 0 :lmax 12} (dissoc (ticks/nice-ticks 0 10 2 [2 4]) :score)))))
