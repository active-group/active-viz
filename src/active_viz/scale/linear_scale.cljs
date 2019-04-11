(ns active-viz.scale.linear-scale
  (:require
   [active-viz.ticks :as ticks]
   [active.clojure.record :as rec :refer-macros [define-record-type]]
   [active-viz.scale.types :as types]))




(define-record-type LinearScaleParams

  (make-linear-scale-params a b) linear-scale-params?
  [a linear-scale-a
   b linear-scale-b]

  types/ScaleFn
  (call [_ _ param]
    (+ (* param a) b))

  (call-inverse [_ _ param]
    (/ (- param b) a)))



(defn make-linear-scale [[domain-min domain-max] [range-min range-max]]
  (let [domain-spread (- domain-max domain-min)
        range-spread  (- range-max range-min)
        a             (/ range-spread domain-spread)
        b             (- range-min (* domain-min a))]
    (types/make-scale domain-min domain-max range-min range-max
      (make-linear-scale-params a b)
      types/nop)))


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
    (types/scale-domain-min scale)
    (types/scale-domain-max scale)
    num-ticks))
