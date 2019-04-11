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


(* (/ (- v domain-min) (- domain-max domain-min)) (- range-max range-min))
(* ())


(defprotocol ScaleFn
  (call [this scale param])
  (call-inverse [this scale param]))


;; Nop scale
;; Signals end of successors

(define-record-type Nop (make-nop) nop? [])
(def nop (make-nop))

