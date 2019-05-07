(ns active-viz.scale.time-scale-test
  (:require  [active-viz.scale.time-scale :as time-scale]
             [cljs-time.core :as time]
             [active-viz.scale :as scale]
             [cljs.test :refer-macros [deftest is testing]]))


(deftest time-scale
  (testing "time scale behaves correctly"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          mid-date   (time/plus start-date (time/days 100))
          s          (time-scale/make-time-scale [start-date end-date] [100 246])]

      (is (= (scale/scale s start-date) 100))
      (is (= (scale/scale s end-date) 246))
      (is (= (scale/scale s mid-date) 140))

      (let [inverted-start (scale/scale-inverted s 100)
            inverted-end   (scale/scale-inverted s 246)
            inverted-mid   (scale/scale-inverted s 140)]
        (is (not (or (time/after? inverted-start  start-date)
                   (time/before? inverted-start  start-date))))
        (is (not (or (time/after? inverted-end  end-date)
                   (time/before? inverted-end  end-date))))
        (is (not (or (time/after? inverted-mid  mid-date)
                   (time/before? inverted-mid  mid-date)))))))


  (testing "time scale are comparable"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          mid-date   (time/plus start-date (time/days 100))
          s          (time-scale/make-time-scale [start-date end-date] [100 246])
          s2         (time-scale/make-time-scale [start-date end-date] [100 246])]

      (is (= s s2))))


  (testing "ticks are generated without crashing"
    (let [start-date (time/date-time 2018 1 1 0 0 0 0)
          end-date   (time/date-time 2019 1 1 0 0 0 0)
          s          (time-scale/make-time-scale [start-date end-date] [100 246])]
      (is (= (count (time-scale/time-scale->ticks s time-scale/month-unit 13)) 13))
      (is (= (count (time-scale/time-scale->ticks s time-scale/month-unit  4)) 5))))


  (testing "units are proposed depending on interval"
    (is (= time-scale/year-unit (time-scale/recommend-unit
                                  (time/date-time 2018 1 1 0 0 0 0)
                                  (time/date-time 2021 1 1 0 0 0 0))))

    (is (= time-scale/month-unit (time-scale/recommend-unit
                                  (time/date-time 2018 1 1 0 0 0 0)
                                  (time/date-time 2018 5 1 0 0 0 0))))

    (is (= time-scale/day-unit   (time-scale/recommend-unit
                                  (time/date-time 2018 1 1 0 0 0 0)
                                  (time/date-time 2018 1 5 0 0 0 0))))

    (is (= time-scale/hour-unit   (time-scale/recommend-unit
                                   (time/date-time 2018 1 1 0 0 0 0)
                                   (time/date-time 2018 1 1 5 0 0 0))))

    (is (= time-scale/minute-unit (time-scale/recommend-unit
                                    (time/date-time 2018 1 1 0 0 0 0)
                                    (time/date-time 2018 1 1 0 5 0 0))))

    (is (= time-scale/second-unit (time-scale/recommend-unit
                                    (time/date-time 2018 1 1 0 0 0 0)
                                    (time/date-time 2018 1 1 0 0 5 0))))

    (is (= time-scale/ms-unit     (time-scale/recommend-unit
                                    (time/date-time 2018 1 1 0 0 0 0)
                                    (time/date-time 2018 1 1 0 0 0 5))))

    )
  )
