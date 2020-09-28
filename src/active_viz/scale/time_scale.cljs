(ns active-viz.scale.time-scale
  (:require [cljs-time.coerce :as coerce]
            [cljs-time.core :as time]
            [cljs-time.format :as format]
            [active-viz.scale.linear-scale :as linear-scale]
            [active.clojure.cljs.record :as rec :refer-macros [define-record-type]]
            [active-viz.ticks :as ticks]
            [active-viz.scale.types :as types])
  (:refer-clojure :exclude [second]))


(rec/define-record-type Unit
  (make-unit time-unit time-constructor approx-ms nice-values) unit?
  [time-unit unit-time-unit
   time-constructor unit-time-constructor
   approx-ms unit-approx-ms
   nice-values unit-nice-values])

(def millisecond 1)
(def second-ms (* 1000 millisecond))
(def minute-ms (* 60 second-ms))
(def hour-ms (* 60 minute-ms))
(def day-ms (* 24 hour-ms))
(def month-ms (* 30 day-ms))
(def year-ms (* 365 day-ms))

(def millisecond-nice [1,5,2,2.5,3,4,1.5,7,6,8,9])
(def second-nice [1 2 3 4 5 10 20 30 60])
(def minute-nice [1 2 5 10 15 30 60])
(def hour-nice [1 2 4 6 8 24])
(def day-nice [1 2 4 6 8 10 15 30 182.5 365])
(def month-nice [1 3])
(def year-nice [1,2,3,4,5,6,7,8,9])


(def year-unit (make-unit time/year time/years year-ms year-nice))
(def month-unit (make-unit time/month time/months month-ms month-nice))
(def day-unit (make-unit time/day time/days day-ms day-nice))
(def hour-unit (make-unit time/hour time/hours hour-ms hour-nice))
(def minute-unit (make-unit time/minute time/minutes minute-ms minute-nice))
(def second-unit (make-unit time/second time/seconds second-ms second-nice))
(def ms-unit (make-unit time/milli time/millis millisecond millisecond-nice))




(define-record-type TimeScaleParams
  (make-time-scale-params linear-scale) time-scale-params?
  [linear-scale time-scale-params-linear-scale]

  types/ScaleFn

  (call [_ _ param]
    (let [param-ms (coerce/to-long param)]
      (types/call (types/scale-params linear-scale) linear-scale param-ms)))

  (call-inverse [_ _ param]
    (coerce/from-long (types/call-inverse (types/scale-params linear-scale) linear-scale param))))


(defn make-time-scale [[domain-min domain-max] [range-min range-max]]
  ;; we will use linear scale internally over the ms passed between the dates
  (let [start-date-ms (coerce/to-long domain-min)
        end-date-ms   (coerce/to-long domain-max)
        linear-scale  (linear-scale/make-linear-scale [start-date-ms end-date-ms] [range-min range-max])]
    (types/make-scale domain-min domain-max range-min range-max
      (make-time-scale-params linear-scale)
      types/nop)))


(defn- find-nice-value [start-date end-date num-ticks unit]
  (->
   (let [unit-ms     (unit-approx-ms unit)
         nice-values (unit-nice-values unit)
         interval    (- (coerce/to-long end-date) (coerce/to-long start-date))
         score-fn    (fn [nice-val] (Math/abs (- num-ticks (/ interval (* nice-val unit-ms)))))]
     (reduce
       (fn [[best-score best-nice-value] nice-value]
         (let [score (score-fn nice-value)]
           (cond
             (and best-score (> best-score score))
             [score nice-value]

             best-score
             [best-score best-nice-value]

             :default
             [score nice-value])))
       [nil nil]
       nice-values))
   clojure.core/second))


(defn t>= [a b]
  (let [a-ms (coerce/to-long a)
        b-ms (coerce/to-long b)]
    (>= a-ms b-ms)))


(defn- time-ceiling [t unit constructor]
  (if (= (coerce/to-long (time/floor t unit)) (coerce/to-long t))
    t
    (time/floor (time/plus t (constructor 1)) unit)))


(defn- create-ticks [start-date end-date num-ticks unit]
  (let [constructor (unit-time-constructor unit)
        nice-values (unit-nice-values unit)
        time-unit   (unit-time-unit unit)
        floor (time/floor start-date time-unit)
        ceiling (time-ceiling end-date time-unit constructor)
        nice-value (find-nice-value floor ceiling num-ticks unit)]
    (loop [acc (list floor)]
      (let [next-date  (time/plus (first acc) (constructor nice-value))
            next-dates (cons next-date acc)]
        (if (t>= next-date end-date)
          next-dates
          (recur next-dates))))))



(defn time-scale->ticks [scale unit num-ticks]
  (let [start-date (types/scale-domain-min scale)
        end-date   (types/scale-domain-max scale)]
    (create-ticks start-date end-date num-ticks unit)))


(def year-formatter (format/formatter "yyyy"))
(defn format-year [dt] (if (= 1 (time/month dt))
                         (format/unparse year-formatter dt)
                         ""))


(def month-formatter (format/formatter "MM/yyyy"))
(defn format-month [dt]
  (format/unparse month-formatter dt)
  #_(if (= 1 (time/day dt))
                          ""))


(defn recommend-unit [start-date end-date]
  (let [interval (- (coerce/to-long end-date) (coerce/to-long start-date))
        gt       (fn [v] (> interval v))]
    (cond
      (gt (* month-ms 36))
      year-unit

      (gt (* day-ms 90))
      month-unit

      (gt (* hour-ms 72))
      day-unit

      (gt (* minute-ms 180))
      hour-unit

      (gt (* second-ms 180))
      minute-unit

      (gt 3000)
      second-unit

      :default
      ms-unit)))
