(ns active-viz.svg.hermite-lines)

(defn path
  "Hermite spline construction.
  Takes a list of points and a list of tangents for each point.
  Expects at least 3 points and tangents.
  Returns a path string."
  [points tangents]

  (let [as-vec   #(if (vector? %) % (vec %))
        tangents (as-vec tangents)
        points   (as-vec points)
        p0       (first points)
        t0       (first tangents)
        p1       (second points)
        t1       (second tangents)

        first-segment (str
                        " M " (first p0) " " (second p0)
                        " C " (+ (first p0) (first t0)) " " (+ (second p0) (second t0))
                        "," (- (first p1) (first t1)) " " (- (second p1) (second t1))
                        "," (first p1) " " (second p1))

        tl (count tangents)]
    (loop [i   2
           acc first-segment]
      (if (< i tl)
        (let [p    (get points i)
              t    (get tangents i)
              acc* (str acc
                     " S " (- (first p) (first t)) " " (- (second p) (second t))
                     "," (first p) " " (second p))]
          (recur (inc i) acc*))
        acc))))
