(ns active-viz.scale.linear-scale-test
  (:require [active-viz.scale.linear-scale :as linear-scale]
            [active-viz.scale :as s]
            [cljs.test :refer-macros [deftest is testing]]))




(deftest linear-scale

  (testing "offset linear scale"
    (let [s (linear-scale/make-linear-scale [0 300] [100 400])
          scale #(s/scale s %)]
      (is (= (scale 0) 100))
      (is (= (scale 100) 200))
      (is (= (scale 200) 300))
      (is (= (scale 300) 400))))

  (testing "stretching linear scale"
    (let [s (linear-scale/make-linear-scale [0 300] [0 600])
          scale #(s/scale s %)]
      (is (= (scale 0) 0))
      (is (= (scale 100) 200))
      (is (= (scale 200) 400))
      (is (= (scale 300) 600))))

  (testing "shrinking linear scale"
    (let [s (linear-scale/make-linear-scale [0 300] [0 150])
          scale #(s/scale s %)]
      (is (= (scale 0) 0))
      (is (= (scale 100) 50))
      (is (= (scale 200) 100))
      (is (= (scale 300) 150))))

  (testing "mixed linear scale"
    (let [s (linear-scale/make-linear-scale [0 300] [50 500])
          scale #(s/scale s %)]
      (is (= (scale 0) 50))
      (is (= (scale 100) 200))
      (is (= (scale 200) 350))
      (is (= (scale 300) 500))))


  (testing "inverting a linear scale"
    (let [s (linear-scale/make-linear-scale [0 100] [100 300])]
      (is (= (s/scale s 0) 100))
      (is (= (s/scale-inverted s 100) 0))
      (is (= (s/scale-inverted s 200) 50)))))
