(ns active-viz.scale.linear-scale
  (:require
   [active-viz.ticks :as ticks]
   [active-viz.scale.util :as util]))

(defn linear-scale [[domain-min domain-max] [range-min range-max]]
  (let [domain-spread (- domain-max domain-min)
        range-spread  (- range-max range-min)]
    (util/make-scale
      domain-min
      domain-max
      range-min
      range-max
      (fn [value]
        (let [normalized (/ (- value domain-min) domain-spread)]
          (+ (* normalized range-spread) range-min))))))


(defn- to-ticks [{:keys [lstep lmin lmax]}]
  (loop [current lmin
         acc [lmin]]
    (let [next (+ lstep (first acc))]
      (if (> next lmax)
        acc
        (recur next (cons next acc))))))


(defn ticks [domain-min domain-max num-ticks]
  (let []
    (println domain-min domain-max)
    (some->
      (ticks/nice-ticks domain-min domain-max num-ticks)
      (to-ticks))))


(defn linear-scale->ticks [scale num-ticks]
  (ticks
    (util/scale-domain-min scale)
    (util/scale-domain-max scale)
    num-ticks))
