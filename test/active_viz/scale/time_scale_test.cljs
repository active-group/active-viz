(ns active-viz.scale.time-scale-test
  (:require  [active-viz.scale.time-scale :as time-scale]
             [cljs-time.core :as time]
             [active-viz.scale.util :as util]
             [cljs.test :refer-macros [deftest is testing]]))


(defn call-scale [scale val]
  ((util/scale-scale-fn scale) val))


(deftest time-scale
  (testing "time scale behaves correctly"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          mid-date   (time/plus start-date (time/days 100))
          s          (time-scale/time-scale [start-date end-date] [100 246])
          scale      #(call-scale s %)]
      (is (= (scale start-date) 100))
      (is (= (scale end-date) 246))
      (is (= (scale mid-date) 140)))))
