(ns active-viz.ticks)

(declare nice-ticks*)

(defn nice-ticks
  "Calculates an interval for ticks and a step-width.
  Uses the Wilkinson Labelling Algorithm as defined in
  \"Wilkinson, L. (2005) The Grammar of Graphics, Springer-Verlag New York, Inc.\"

  Takes

  * `dmin`, a lower bound of the value range
  * `dmax`, an upper bound of the value range
  * `m` is the target number of ticks in value range. This is a rough suggestion and will the real number of ticks is determined in the range `mrange`
  * `q` (optional), a vector of nice values, which are used to calculate intra-tick distance
  * `min-coverage` (optional) as a algorithm parameter, determining a minimum of interval coverage, defaults to 0.8
  * `mrange` (optional) the range in which the number of ticks is determined. This defaults to `(/ m 2)` and `(* m 3)`


  Returns a map containing `:lmin`, `:lmax` and `:lstep`, where lmin is the lower bound of
  the interval lmax the upperbound and lstep the step-width.
  Also returns a score value in `:score`. The higher the score the better the result. "
  ([dmin dmax m]
   (nice-ticks dmin dmax m [1,5,2,2.5,3,4,1.5,7,6,8,9,10]))
  ([dmin dmax m q]
   (nice-ticks dmin dmax m q 0.8 (range (Math/max (Math/floor (/ m 2)) 2) (Math/ceil (* 3 m)))))
  ([dmin dmax m q min-coverage mrange]
   (reduce
     (fn [best k]
       (let [result (nice-ticks* dmin dmax k q min-coverage mrange m)]
         (if (or
               (and (not (nil? result)) (nil? best))
               (> (:score result) (:score best)))
           result
           best)))
     nil
     mrange)))


(defn- nice-ticks* [min max k q min-coverage mrange m]
  (let [q (vec q)
        range-vals (- max min)
        intervals (dec k)

        granularity (dec (/ (Math/abs (- k m)) m))

        delta (/ range-vals intervals)
        base (Math/floor (Math/log10 delta))
        dbase (Math/pow 10 base)]
    (reduce
      (fn [best i]
        (let [tdelta (* (get q i) dbase)
              tmin (* tdelta (Math/floor (/ min tdelta)))
              tmax (+ tmin (* intervals tdelta))]
          (if (and (<= tmin min) (>= tmax max))
            (let [p         (if (and (<= tmin 0) (>= tmax 0)) 1 0)
                  roundness (- 1 (/ (- (dec i) p) (count q)))
                  coverage  (/ (- max min) (- tmax tmin))]
              (if (> coverage min-coverage)
                (let [tnice (+ granularity roundness coverage)]
                  (if (or (nil? best) (> tnice (:score best)))
                    {:lmin tmin
                     :lmax tmax
                     :lstep tdelta
                     :score tnice}
                    best))
                best))
            best)))
      nil
      (range 0 (count q)))))
