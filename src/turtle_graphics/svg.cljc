(ns turtle-graphics.svg
  (:require
   [complex.vector :as v]))

(def point-options {:stroke "grey" :fill "red"})

(defn svg-point
  ([p] (svg-point p point-options))
  ([[x y] options]
   (let [{:keys [stroke fill]} options]
     [:circle {:stroke stroke
               :fill fill
               :cx x
               :cy y
               :r 3}])))

(defn svg-line
  ([p1 p2] (svg-line p1 p2 {:stroke "black"}))
  ([p1 p2 {:keys [stroke]}]
   (let [[x1 y1] p1
         [x2 y2] p2]
     [:line {:x1 x1 :x2 x2 :y1 y1 :y2 y2 :stroke stroke}])))

(defn minus [v w]
  (v/sum v (v/scal-mul -1 w)))

(defn normalize [v]
  (v/scal-mul (/ (v/len v)) v))

(defn arrow-tips [position endpoint]
  (let [v (minus endpoint position)
        v1 (v/scal-mul 10 (normalize v))
        [vx vy] v1
        vp1 [(- vy) vx]
        vp2 [vy (- vx)]
        av1 (minus vp1 v1)
        av2 (minus vp2 v1)]
    [(v/sum endpoint av1)
     (v/sum endpoint av2)]))

(defn turtle->svg
  "return svg for given turtle and t-fn"
  [position endpoint t-fn]
  (let [position (t-fn position)
        endpoint (t-fn endpoint)
        [at1 at2] (arrow-tips position endpoint)]
    [:g
     (svg-point position)
     (svg-line position endpoint)
     (svg-line endpoint at1) (svg-line endpoint at2)]))
