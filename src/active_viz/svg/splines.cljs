(ns active-viz.svg.splines)

(defn path
  "Takes a list of points and a list of control points, containing
  tuples of control points for each point respectively.
  Returns a svg path string."
  [points cps]

  (let [[x0 x1] (first points)

        points'         (rest points)
        control-points' (rest cps)
        p+cp            (map vector points' control-points')
        path            (str "M " x0 " " x1)]
    (reduce
      (fn [acc [[x y] [[cx0 cy0] [cx1 cy1]]]]
        (str acc " C " cx0 " " cy0 "," cx1 " " cy1 " " x " " y))
      path
      p+cp)))
