(ns active-viz.scale
  (:require [active.clojure.record :as rec :refer-macros [define-record-type]]
            [active.clojure.lens :as lens :include-macros true]))



(define-record-type
  ^{:doc "A scale represents a function from a domain described by `domain-min` and `domain-max` into a
     a target range between `range-min` and `range-max`. The range values must be numerical.
     Domain boundaries as well as range boundaries are expected to be ordered correctly.
     The actual mapping is defined in `scale-fn`"}
  Scale
  (make-scale domain-min domain-max range-min range-max params succ)
  scale?
  [^{:doc "minimum domain value"} domain-min scale-domain-min
   ^{:doc "maximum domain value"} domain-max scale-domain-max
   ^{:doc "minimum target range value. Expected to be numerical."} range-min scale-range-min
   ^{:doc "maximum target range value. Expected to be numerical."} range-max scale-range-max
   ^{:doc "mapping params from domain to range, defining the type of scale"} params scale-params
   ^{:doc "successor of the scale, when combined"} succ scale-succ])


(defprotocol ScaleFn
  (call [this scale param])
  (call-inverse [this scale param]))


;; Nop scale
;; Signals end of successors

(define-record-type Nop (make-nop) nop? [])
(def nop (make-nop))



;; Inverting Scale

(define-record-type InvertingScaleParams
  (make-inverting-scale-params min+max) inverting-scale-params?
  [min+max inverting-scale-params-min+max]

  ScaleFn

  (call [this _ param]
    (- (inverting-scale-params-min+max this) param))

  (call-inverse [this s param]
    (call this s param)))




; Padding Scale
(define-record-type PaddingScaleParams
  (make-padding-scale-params padding) padding-scale-params?
  [padding padding-scale-params-padding]

  ScaleFn

  (call [this _ param]
    (+ param (padding-scale-params-padding this)))

  (call-inverse [this _ param]
    (- param (padding-scale-params-padding this))))


(defn- collect-scales-reversed [scale]
  (loop [s   scale
         acc (list)]
    (if (nop? s)
      acc
      (recur
        (scale-succ s)
        (cons s acc)))))


(defn scale
  "Scales a value using the given scale"
  [scale value]
  (loop [s scale
         p value]
    (if (nop? s)
      p
      (let [scale-params (scale-params s)]
        (recur
          (scale-succ s)
          (call scale-params s p))))))



(defn scale-inverted
  "Scales the value inverted (reversed) using the given scale"
  [scale param]
  (reduce
    (fn [param' s]
      (call-inverse (scale-params s) s param'))
    param
    (collect-scales-reversed scale)))


(defn >>
  "Combines two scales"
  [scale1 scale2]
  (let [succ (scale-succ scale1)]
    (if (nop? succ)
      (lens/shove scale1 scale-succ scale2)
      (lens/shove scale1 scale-succ (>> succ scale2)))))


(defn invert
  "Inverts the range of a scaling for the specified range.
  This is done by calculating the center of this range and mirroring target values.
  Thus, ranges must be defined correctly, thus.
  "
  [scale]
  (let [min             (scale-range-min scale)
        max             (scale-range-max scale)
        min+max         (+ min max)
        inverting-scale (make-scale
                          (scale-domain-min scale)
                          (scale-domain-max scale)
                          min
                          max
                          (make-inverting-scale-params min+max)
                          nop)]
    (>> scale inverting-scale)))



(defn pad
  "Pads a scale in the sense of adding a offset defined in `padding` to the target range."
  [scale padding]
  (let [prev-min        (scale-range-min scale)
        prev-max        (scale-range-max scale)
        min             (+ prev-min padding)
        max             (+ prev-max padding)
        padding-scale (make-scale
                        (scale-domain-min scale)
                        (scale-domain-max scale)
                        min
                        max
                        (make-padding-scale-params padding)
                        nop)]
    (>> scale padding-scale)))



(defn pad-to-zero
  "Pads a scale to 0. That is, it subtracts or adds an offset to the smallest range boundary, setting it to 0"
  [scale]
  (letfn [(negate [v] (* -1 v))]
    (->> (min (scale-range-min scale) (scale-range-max scale))
      (negate)
      (pad scale))))



(defn range
  "Returns the range of a scale. That is the range of the last scale in succ"
  [scale]
  (let [last (loop [s scale]
               (if (nop? (scale-succ s))
                 s
                 (recur (scale-succ s))))]
    [(scale-range-min last) (scale-range-max last)]))



(defn adjust-range
  "Simply sets a new range in the last succ of scale, without altering anything else"
  [scale [range-min range-max]]
  (if (nop? (scale-succ scale))
    (-> scale
      (lens/shove scale-range-min range-min)
      (lens/shove scale-range-max range-max))
    (lens/shove scale scale-succ (adjust-range (scale-succ scale) [range-min range-max]))))



(defn domain
  "Returns the domain of a scale. That is the domain of the first scale"
  [scale]
  [(scale-domain-min scale) (scale-domain-max scale)])
