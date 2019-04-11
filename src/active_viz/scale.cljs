(ns active-viz.scale
  (:refer-clojure :exclude [range])
  (:require [active.clojure.record :as rec :refer-macros [define-record-type]]
            [active-viz.scale.types :as types]
            [active-viz.scale.linear-scale :as linear-scale]
            [active-viz.scale.time-scale :as time-scale]
            [active.clojure.lens :as lens :include-macros true]))




(defn make-linear-scale [[domain-min domain-max] [range-min range-max]]
  (linear-scale/make-linear-scale [domain-min domain-max] [range-min range-max]))


(defn make-time-scale [[domain-min domain-max] [range-min range-max]]
  ;; we will use linear scale internally over the ms passed between the dates
  (time-scale/make-time-scale [domain-min domain-max] [range-min range-max]))


(defn- collect-scales-reversed [scale]
  (loop [s   scale
         acc (list)]
    (if (types/nop? s)
      acc
      (recur
        (types/scale-succ s)
        (cons s acc)))))


(defn scale
  "Scales a value using the given scale."
  [scale value]
  (loop [s scale
         p value]
    (if (types/nop? s)
      p
      (let [scale-params (types/scale-params s)]
        (recur
          (types/scale-succ s)
          (types/call scale-params s p))))))



(defn scale-inverted
  "Scales the value inverted (reversed) using the given scale."
  [scale param]
  (reduce
    (fn [param' s]
      (types/call-inverse (types/scale-params s) s param'))
    param
    (collect-scales-reversed scale)))


(defn >>
  "Combines two scales. Note that in evaluation scale1 is evaluated first."
  [scale1 scale2]
  (let [succ (types/scale-succ scale1)]
    (if (types/nop? succ)
      (lens/shove scale1 types/scale-succ scale2)
      (lens/shove scale1 types/scale-succ (>> succ scale2)))))


(defn range
  "Returns the range of a scale. That is the range of the last scale in succ."
  [scale]
  (let [last (loop [s scale]
               (if (types/nop? (types/scale-succ s))
                 s
                 (recur (types/scale-succ s))))]
    [(types/scale-range-min last) (types/scale-range-max last)]))


(defn invert
  "Inverts the range of a scaling for the specified range.
  This is done by calculating the center of this range and mirroring target values.
  Thus, ranges must be defined correctly, thus."
  [scale]
  (let [[min max]       (range scale)
        inverting-scale (make-linear-scale [min max] [max min])]
    (>> scale inverting-scale)))



(defn pad
  "Pads a scale in the sense of adding a offset defined in `padding` to the target range."
  [scale padding]
  (let [[min max]     (range scale)
        padding-scale (make-linear-scale [min max] [(+ padding min) (+ padding max)])]
    (>> scale padding-scale)))


(defn pad-to-zero
  "Pads a scale to 0. That is, it subtracts or adds an offset to the smallest range boundary, setting it to 0"
  [scale]
  (letfn [(negate [v] (* -1 v))]
    (->> (min (types/scale-range-min scale) (types/scale-range-max scale))
      (negate)
      (pad scale))))




(defn adjust-range
  "Simply sets a new range in the last succ of scale, without altering anything else"
  [scale [range-min range-max]]
  (if (types/nop? (types/scale-succ scale))
    (-> scale
      (lens/shove types/scale-range-min range-min)
      (lens/shove types/scale-range-max range-max))
    (lens/shove scale types/scale-succ (adjust-range (types/scale-succ scale) [range-min range-max]))))



(defn domain
  "Returns the domain of a scale. That is the domain of the first scale"
  [scale]
  [(types/scale-domain-min scale) (types/scale-domain-max scale)])


(defn linear-scale->ticks [scale num-ticks]
  (linear-scale/linear-scale->ticks scale num-ticks))


(defn time-scale->ticks [scale type num-ticks]
  ;; Type can be: :year :month :week :day :hour :minute :second :millisecond
  (time-scale/time-scale->ticks scale type num-ticks))
