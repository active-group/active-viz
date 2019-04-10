(ns active-viz.scale.linear-scale
  (:require
   [active-viz.ticks :as ticks]
   [active.clojure.record :as rec :refer-macros [define-record-type]]
   [active-viz.scale :as scale]))



(defn apply-linear-scale [domain-spread range-spread domain-min range-min value]
  (let [normalized (/ (- value domain-min) domain-spread)]
    (+ (* normalized range-spread) range-min)))




(define-record-type LinearScaleParams
  (make-linear-scale-params domain-spread range-spread) linear-scale-params?
  [domain-spread linear-scale-params-domain-spread
   range-spread linear-scale-params-range-spread]

  scale/ScaleFn

  (call [this scale param]
    (let [domain-min (scale/scale-domain-min scale)
          range-min  (scale/scale-range-min scale)

          domain-spread (linear-scale-params-domain-spread this)
          range-spread  (linear-scale-params-range-spread this)]
      (apply-linear-scale domain-spread range-spread domain-min range-min param)))

  (call-inverse [this scale param]
    (let [domain-min (scale/scale-domain-min scale)
          range-min  (scale/scale-range-min scale)

          domain-spread (linear-scale-params-domain-spread this)
          range-spread  (linear-scale-params-range-spread this)]
      (apply-linear-scale range-spread domain-spread range-min domain-min param))))




(defn make-linear-scale [[domain-min domain-max] [range-min range-max]]
  (let [domain-spread (- domain-max domain-min)
        range-spread  (- range-max range-min)]
    (scale/make-scale
      domain-min
      domain-max
      range-min
      range-max
      (make-linear-scale-params domain-spread range-spread)
      scale/nop)))


(defn- to-ticks [{:keys [lstep lmin lmax]}]
  (loop [current lmin
         acc [lmin]]
    (let [next (+ lstep (first acc))]
      (if (> next lmax)
        acc
        (recur next (cons next acc))))))


(defn- ticks [domain-min domain-max num-ticks]
  (let []
    (some->
      (ticks/nice-ticks domain-min domain-max num-ticks)
      (to-ticks))))


(defn linear-scale->ticks [scale num-ticks]
  (ticks
    (scale/scale-domain-min scale)
    (scale/scale-domain-max scale)
    num-ticks))
