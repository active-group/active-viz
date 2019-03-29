(ns active-viz.scale.util-test
  (:require [active-viz.scale.util :as util]
            [active-viz.scale.linear-scale :as linear-scale]
            [active-viz.scale.time-scale :as time-scale]
            [cljs-time.core :as time]
            [cljs.test :refer-macros [deftest is testing]]))


(defn call-scale [scale val]
  ((util/scale-scale-fn scale) val))


(deftest util
  (testing "combines two scales properly"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          time-scale (time-scale/time-scale [start-date end-date] [100 246])
          linear-scale (linear-scale/linear-scale [100 246] [0 1000])
          combined-scale (util/combine linear-scale time-scale)]
      (is (= (call-scale combined-scale start-date) 0))
      (is (= (call-scale combined-scale end-date) 1000))))


  (testing "inverts a scale range"
    (let [scale (linear-scale/linear-scale [0 100] [0 200])
          inverted (util/invert-scale-range scale)]
      (is (= (call-scale inverted 0) 200))
      (is (= (call-scale inverted 50) 100))
      (is (= (call-scale inverted 100) 0))))


  (testing "pads a scale"
    (let [scale (linear-scale/linear-scale [0 100] [0 200])
          pad-scale (util/pad-scale scale 50)]
      (is (= (call-scale pad-scale 0) 50))
      (is (= (call-scale pad-scale 50) 150))
      (is (= (call-scale pad-scale 100) 250))))


  (testing "pads a scale to zero"
    (let [neg-scale (linear-scale/linear-scale [-100 100] [-200 200])
          pad-scale (util/pad-zero neg-scale)

          pos-scale (linear-scale/linear-scale [-100 100] [200 600])
          pad-scale-2 (util/pad-zero pos-scale)]
      (is (= (call-scale pad-scale -100) 0))
      (is (= (call-scale pad-scale 100) 400))
      (is (= (call-scale pad-scale-2 -100) 0))
      (is (= (call-scale pad-scale-2 100) 400)))))
