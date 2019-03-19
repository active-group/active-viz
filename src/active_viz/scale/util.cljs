(ns active-viz.scale.util
  (:require [active.clojure.record :as rec :refer-macros [define-record-type]]))


(define-record-type
  ^{:doc "A scale represents a function from a domain described by `domain-min` and `domain-max` into a
     a target range between `range-min` and `range-max`. The range values must be numerical.
     Domain boundaries as well as range boundaries are expected to be ordered correctly.
     The actual mapping is defined in `scale-fn`"}
  Scale
  (make-scale domain-min domain-max range-min range-max scale-fn)
  scale?
  [^{:doc "minimum domain value"} domain-min scale-domain-min
   ^{:doc "maximum domain value"} domain-max scale-domain-max
   ^{:doc "minimum target range value. Expected to be numerical."} range-min scale-range-min
   ^{:doc "maximum target range value. Expected to be numerical."} range-max scale-range-max
   ^{:doc "mapping function from domain to range"} scale-fn scale-scale-fn])


(defn combine
  "Combines two scales. The resulting scale maps the domain of scale1 to the
  range of scale2. `scale-fn` is simply the composition of both scales' `scale-fn`s"
  [scale1 scale2]
  (make-scale
    (scale-domain-min scale2)
    (scale-domain-max scale2)
    (scale-range-min scale1)
    (scale-range-max scale1)
    (comp (scale-scale-fn scale1) (scale-scale-fn scale2))))


(defn invert-scale-range
  "Inverts the range of a scaling, including the invertion of the scale-fn, for the specified range.
  This is done by calculating the center of this range and mirroring target values."
  [scale]
  (let [min          (scale-range-min scale)
        max          (scale-range-max scale)
        min+max      (+ min max)
        scale-fn     #(- min+max %)
        invert-scale (make-scale min max min max scale-fn)]
    (combine invert-scale scale)))


(defn pad-scale
  "Pads a scale in the sense of adding a offset defined in `padding` to the target range."
  [scale padding]
  (let [prev-min      (scale-range-min scale)
        prev-max      (scale-range-max scale)
        min           (+ padding prev-min)
        max           (+ padding prev-max)
        scale-fn      #(+ padding %)
        padding-scale (make-scale prev-min prev-max min max scale-fn)]
    (combine padding-scale scale)))


(defn pad-zero
  "Pads a range so that it maps to positive values, starting at zero."
  [scale]
  (letfn [(negate [v] (* -1 v))]
   (->> (min (scale-range-min scale) (scale-range-max scale))
     (negate)
     (pad-scale scale))))


