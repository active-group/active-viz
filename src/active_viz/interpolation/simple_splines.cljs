(ns active-viz.interpolation.simple-splines
  (:require
   [active-viz.interpolation.util :as util]))


(defn line [point1 point2]
  {:length (util/distance point1 point2)
   :angle  (util/angle point1 point2)})

(defn- control-point [previous current next reverse? smoothness]
  (let [p           (or previous current)
        n           (or next current)
        l           (line p n)
        line-angle  (+ (:angle l) (if reverse? Math/PI 0))
        line-length (* smoothness (:length l))
        [cx cy]     current]
    [(+ cx (* line-length (Math/cos line-angle)))
     (+ cy (* line-length (Math/sin line-angle)))]))


(defn- points->control-points [p-2 p-1 p p+1 p+2 smoothness]
  (let [cp1 (control-point p-2 p-1 p false smoothness)
        cp2 (control-point p-1 p p+1 true smoothness)]
    [cp1 cp2]))


(defn interpolate
  "Does a simple splines interpolation via control points by
  using the angle and distance between successive points. \n
  Takes a list of points (`[x y]`) and returns a list of spline
  control-points tuples for each point in `points`. \n

  The factor smoothness defines the smoothing between points, where
  a high value results in high smoothing. We recommend values between 0 and 1,
  however, these highly depend on the data in `points`. "
  [points smoothness]
  (let [l      (count points)
        points (vec points)]
    (-> (loop [acc []
               i   0]
          (if (>= i l)
            acc
            (let [p-2 (get points (- i 2))
                  p-1 (get points (dec i))
                  p   (get points i)
                  p+1 (get points (inc i))
                  p+2 (get points (+ i 2))]
              (recur
                (cons (points->control-points p-2 p-1 p p+1 p+2 smoothness) acc)
                (inc i)))))
      (reverse))))
