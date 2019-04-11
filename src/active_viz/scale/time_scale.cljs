(ns active-viz.scale.time-scale
  (:require [cljs-time.coerce :as coerce]
            [cljs-time.core :as time]
            [cljs-time.format :as format]
            [active-viz.scale.linear-scale :as linear-scale]
            [active.clojure.record :as rec :refer-macros [define-record-type]]
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
(def ms-unit (make-unit time/millis time/millis millisecond millisecond-nice))




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


(defn- indivisible-unit-constructor? [constr]
  (or
    (= constr time/months)
    (= constr time/years)))

(defn- round-indivisible [v]
  (max 1 (Math/floor v)))


(defn- round [a] (/ (Math/round (* 1000000 a)) 1000000))

(defn- create-time-ticks [{:keys [lmin lmax lstep]} first-date last-date
                         time-unit-constructor]

  (let [fix-indivisible? (and (indivisible-unit-constructor? time-unit-constructor)
                           (not (int? lstep)))
        ticks-step   (if fix-indivisible?
                       (round-indivisible lstep)
                       (round lstep))

        inc-datetime #(time/plus % (time-unit-constructor ticks-step))]

    (-> (loop [current lmin
               acc     []]
          (if (<= current lmax)
            (recur
              (round (+ current ticks-step))
              (if (empty? acc)
                [first-date]
                (cons (inc-datetime (first acc))  acc)))
            (if (< (coerce/to-long (first acc)) (coerce/to-long last-date))
              (cons (inc-datetime (first acc))  acc)
              acc)))
      reverse)))


(defn- create-ticks [start-date end-date num-ticks unit]
  (let [constructor (unit-time-constructor unit)
        nice-values (unit-nice-values unit)
        unit-ms (unit-approx-ms unit)
        time-unit (unit-time-unit unit)

        first-ticks-date (time/floor start-date time-unit)
        first-ticks-date-ms (coerce/to-long first-ticks-date)
        relative-start-date-ms (- (coerce/to-long start-date) first-ticks-date-ms)
        relative-end-date-ms (- (coerce/to-long end-date) first-ticks-date-ms)]
    (some->
      (ticks/nice-ticks (/ relative-start-date-ms unit-ms) (/ relative-end-date-ms unit-ms)
        num-ticks nice-values)
      (create-time-ticks first-ticks-date end-date constructor))))


(defn- choose-unit [kw]
  (case kw
    :year        year-unit
    :month       month-unit
    :day         day-unit
    :hour        hour-unit
    :minute      minute-unit
    :second      second-unit
    :millisecond ms-unit))


(defn time-scale->ticks [scale type num-ticks]
  ;; Type can be: :year :month :week :day :hour :minute :second :millisecond
  (let [start-date (types/scale-domain-min scale)
        end-date   (types/scale-domain-max scale)
        start-date-ms (coerce/to-long start-date)
        end-date-ms (coerce/to-long end-date)
        unit (choose-unit type)]
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
