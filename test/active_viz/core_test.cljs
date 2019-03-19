(ns active-viz.core-test
    (:require
     [active-viz.scale.linear-scale :as linear-scale]
     [active-viz.scale.time-scale :as time-scale]
     [active-viz.scale.util :as util]
     [active-viz.ticks :as ticks]
     [cljs-time.core :as time]
     [cljs.test :refer-macros [deftest is testing]]))

(defn call-scale [scale val]
  ((util/scale-scale-fn scale) val))


(deftest linear-scale

  (testing "offset linear scale"
    (let [s (linear-scale/linear-scale [0 300] [100 400])
          scale #(call-scale s %)]
      (is (= (scale 0) 100))
      (is (= (scale 100) 200))
      (is (= (scale 200) 300))
      (is (= (scale 300) 400))))

  (testing "stretching linear scale"
    (let [s (linear-scale/linear-scale [0 300] [0 600])
          scale #(call-scale s %)]
      (is (= (scale 0) 0))
      (is (= (scale 100) 200))
      (is (= (scale 200) 400))
      (is (= (scale 300) 600))))

  (testing "shrinking linear scale"
    (let [s (linear-scale/linear-scale [0 300] [0 150])
          scale #(call-scale s %)]
      (is (= (scale 0) 0))
      (is (= (scale 100) 50))
      (is (= (scale 200) 100))
      (is (= (scale 300) 150))))

  (testing "mixed linear scale"
    (let [s (linear-scale/linear-scale [0 300] [50 500])
          scale #(call-scale s %)]
      (is (= (scale 0) 50))
      (is (= (scale 100) 200))
      (is (= (scale 200) 350))
      (is (= (scale 300) 500)))))


(deftest date-scale
  (testing "date scale"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          mid-date   (time/plus start-date (time/days 100))
          s          (time-scale/time-scale [start-date end-date] [100 246])
          scale      #(call-scale s %)]
      (is (= (scale start-date) 100))
      (is (= (scale end-date) 246))
      (is (= (scale mid-date) 140)))))



(deftest ticks-test
  (testing "generates ticks based on prefered amount"
    ;; Testing with m=4 here since upper bound is 3*4 which results in 11 ticks
    (is (= {:lstep 1 :lmin 0 :lmax 10} (dissoc (ticks/nice-ticks 0 10 4) :score)))
    (is (= {:lstep 2.5 :lmin 0 :lmax 10} (dissoc (ticks/nice-ticks 0 10 2) :score))))

  (testing "generates ticks based on passed nice vectors"
    (is (= {:lstep 2 :lmin 0 :lmax 12} (dissoc (ticks/nice-ticks 0 10 4 [2 4]) :score)))
    (is (= {:lstep 4 :lmin 0 :lmax 12} (dissoc (ticks/nice-ticks 0 10 2 [2 4]) :score)))))



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
