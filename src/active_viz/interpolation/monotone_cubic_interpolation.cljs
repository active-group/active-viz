(ns active-viz.interpolation.monotone-cubic-interpolation
  (:require [active-viz.interpolation.util :as util]))

(defn- normalize-tagents
  "Compute the normalized tangent vector from the slopes. Note that if x is
  not monotonic, it's possible that the slope will be infinite, so we protect
  against NaN by setting the coordinate to zero."
  [points m smoothness]
  (letfn [(f [p-1 p+1 m]
            (let [[x1 _] p-1
                  [x2 _] p+1
                  q      (* smoothness (inc (Math/pow m 2)))
                  s      (/ (- x2 x1) q)]
              [(util/number-or-zero s) (util/number-or-zero (* m s))]))]

    (let [c (count m)]
      (map-indexed
        (fn [idx m']
          (cond
            (= 0 idx) ; first
            (f (first points) (get points (inc idx)) m')

            (= (dec idx) c) ; last
            (f (get points (dec idx)) (last points) m')

            :else ; intermediate
            (f (get points (dec idx)) (get points (inc idx)) m')))
        m))))


(defn interpolate
  "Interpolates the given points using Fritsch-Carlson Monotone cubic Hermite interpolation. \n

  Expects at least 3 points sorted by the x coordinate. \n

  An optional parameter \"smoothness\" can be specified to adjust smoothing. A value between 2 and 10 is
  recommended, where 2 is very smooth and 10 less smooth. \n
  Note: A proper smoothing factor depends on the data-scale described by points. Defaults to 6. \n

  Returns an array of tangent vectors (which is a vector of length 2). \n

  For details, see http://en.wikipedia.org/wiki/Monotone_cubic_interpolation"
  [points & [smoothness]]
  (let [smoothness (or smoothness 6)
        points (if (vector? points) points (vec points))
        fds    (util/finite-differences points)
        p+fds  (map vector points fds)

        [p0 m0] (first p+fds)
        p+fds* (rest p+fds)]
    (->> (reduce
       (fn [[acc prev-point] [point m]]
         (let [prev-m (first acc)
               acc*   (rest acc)
               d      (util/slope prev-point point)]
           (if (> util/eps (Math/abs d))
             ;; If two consec points are equal (d is zero) set the tangent to
             ;; 0 to preserve monotonicity
             [(cons 0 (cons 0 acc*)) point]
             ;; Prevent overshoot too ensure monotonicity by restricting
             ;; the magnitude to circle radius 3 in (1)
             ;; else return the tangents (2).
             (let [a (/ prev-m d)
                   b (/ m d)
                   s (+ (* b b) (* a a))]
               (if (> s 9)
                 ;; (1)
                 (let [s       (/ (* d 3) (Math/sqrt s))
                       m-prev* (* s a)
                       m*      (* s b)]
                   [(cons m* (cons m-prev* acc*)) point])
                 ;; (2)
                 [(cons m acc) point])))))
       [[m0] p0]
       p+fds*)
      (first)
      (reverse)
      ((fn [p] (normalize-tagents (vec points) p smoothness))))))
