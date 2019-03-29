(ns active-viz.interpolation.util)

(def eps js/Number.EPSILON)

(defn slope
  "Computes the slope described by two points"
  [[x1 y1] [x2 y2]]
  (/ (- y2 y1) (- x2 x1)))


(defn- finite-differences
  "Computes the 3 point differences for each point
  https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Finite_difference "
  [points]
  (let [p0      (first points)
        p1      (second points)
        points* (drop 2 points)
        d       (slope p0 p1)]
    (-> (reduce
          (fn [[acc prev-point] point]
            (let [d  (first acc)
                  d* (/ (+ d (slope prev-point point)) 2)]
              [(cons d* acc) point]))
          [[d] p1]
          points)
      first
      reverse)))


(defn number-or-zero
  "If n is NaN it returns 0 else n"
  [n]
  (if (js/Number.isNaN n)
    0
    n))




(defn distance [[x1 y1] [x2 y2]]
  (Math/sqrt (+ (Math/pow (- x2 x1) 2) (Math/pow (- y2 y1) 2))))

(defn angle [[x1 y1] [x2 y2]]
  (Math/atan2 (- y2 y1) (- x2 x1)))
