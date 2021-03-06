(ns turtle-graphics.svg
  (:require
   [turtle-graphics.transforms :as t]
   [complex.number :as n]
   [complex.vector :as v]
   [cljs.core.match :refer-macros [match]]))

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

(defn svg-command->string [command t-fn]
  (match command
         [:M p]
         (let [[px py] (t-fn p)]
           (str "M " px " " py " "))
         [:L p]
         (let [[px py] (t-fn p)]
           (str "L " px " " py " "))
         :else nil))

(defn svg-command->text [command]
  (match command
         [:M p]
         (let [[px py] (n/coords p)]
           (str "M " px " " py " "))
         [:L p]
         (let [[px py] (n/coords p)]
           (str "L " px " " py " "))
         :else nil))

(defn svg-path-string [svg-commands t-fn]
  (clojure.string/trim
   (clojure.string/join
    (map #(svg-command->string % t-fn) svg-commands))))

(defn svg-path-text [svg-commands]
  (clojure.string/trim
   (clojure.string/join
    (map svg-command->text svg-commands))))

(defn svg-circle [circle t-fn]
  (let [{:keys [stroke fill center radius]} circle
        [cx cy] (t-fn center)]
    [:circle {:stroke (t/color-table stroke)
              :fill (t/color-table fill)
              :cx cx
              :cy cy
              :r (t-fn radius)}]))

(defn svg-point2 [circle t-fn]
  (let [{:keys [stroke fill center radius]} circle
        [cx cy] (t-fn center)]
    [:circle {:stroke (t/color-table stroke)
              :fill (t/color-table fill)
              :cx cx
              :cy cy
              :r 3}]))

(defn svg-line2 [line t-fn]
  (let [{:keys [p1 p2]} line
        [x1 y1] (t-fn p1)
        [x2 y2] (t-fn p2)]
    [:line {:stroke "pink"
            :x1 x1 :y1 y1
            :x2 x2 :y2 y2}]))

(defn svg-circles [app t-fn]
  (let [circles (get-in app [:svg :circles])]
    (into [:g {:className "circle-group"}] (map #(svg-circle % t-fn) circles))))

(defn svg-points [app t-fn]
  (let [points (get-in app [:svg :points])]
    (into [:g {:className "point-group"}] (map #(svg-point2 % t-fn) points))))

(defn svg-lines [app t-fn]
  (let [lines (get-in app [:lines])]
    (into [:g {:className "line-group"}] (map #(svg-line2 % t-fn) lines))))

(defn svg-path [app t-fn]
  (let [svg-commands (get-in app [:svg :path])
        path-string (svg-path-string svg-commands t-fn)]
    [:path {:d path-string
            :stroke "black" :fill "white"}]))
