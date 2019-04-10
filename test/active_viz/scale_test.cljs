(ns active-viz.scale-test
  (:require [active-viz.scale :as scale]
            [active-viz.scale.linear-scale :as linear-scale]
            [active-viz.scale.time-scale :as time-scale]
            [cljs-time.core :as time]
            [cljs.test :refer-macros [deftest is testing]]))


(deftest scale

  (testing "combines two scales properly"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          time-scale (time-scale/make-time-scale [start-date end-date] [100 246])
          linear-scale (linear-scale/make-linear-scale [100 246] [0 1000])
          combined-scale (scale/>> time-scale linear-scale)]

      (is (= (scale/scale combined-scale start-date) 0))
      (is (= (scale/scale combined-scale end-date) 1000))))


  (testing "inverts a scale range"
    (let [linear-scale (linear-scale/make-linear-scale [0 100] [0 200])
          inverted (scale/invert linear-scale)
          orig (scale/invert inverted)]
      (is (= (scale/scale inverted 0) 200))
      (is (= (scale/scale inverted 50) 100))
      (is (= (scale/scale inverted 100) 0))
      (is (= (scale/scale linear-scale 25) (scale/scale orig 25)))
      (is (= (scale/scale-inverted inverted 0) 100))
      (is (= (scale/scale-inverted inverted 100) 50))
      (is (= (scale/scale-inverted inverted 200) 0))))


  (testing "pads a scale"
    (let [scale (linear-scale/make-linear-scale [0 100] [0 200])
          pad-scale (scale/pad scale 50)]
      (is (= (scale/scale pad-scale 0) 50))
      (is (= (scale/scale pad-scale 50) 150))
      (is (= (scale/scale pad-scale 100) 250))
      (is (= (scale/scale-inverted pad-scale 50) 0))
      (is (= (scale/scale-inverted pad-scale 150) 50))
      (is (= (scale/scale-inverted pad-scale 250) 100))))


  (testing "pads a scale to zero"
    (let [neg-scale (linear-scale/make-linear-scale [-100 100] [-200 200])
          pad-scale (scale/pad-to-zero neg-scale)

          pos-scale (linear-scale/make-linear-scale [-100 100] [200 600])
          pad-scale-2 (scale/pad-to-zero pos-scale)]

      (is (= (scale/scale pad-scale -100) 0))
      (is (= (scale/scale pad-scale 100) 400))
      (is (= (scale/scale pad-scale-2 -100) 0))
      (is (= (scale/scale pad-scale-2 100) 400))

      (is (= (scale/scale-inverted pad-scale 0) -100))
      (is (= (scale/scale-inverted pad-scale 400) 100))
      (is (= (scale/scale-inverted pad-scale-2 0) -100))
      (is (= (scale/scale-inverted pad-scale-2 400) 100)))))
