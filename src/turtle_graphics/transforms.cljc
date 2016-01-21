(ns turtle-graphics.transforms
  "user to screen transform functions and color keyword lookup"
  (:require [complex.vector :as v]
            [complex.number :as n]
            [thi.ng.color.core :as col]))

;; color values
(def red      (col/rgba 1 0 0 1.0))
(def lt-red   (col/rgba 1 0 0 0.25))
(def green    (col/rgba 0 1 0 1.0))
(def lt-green (col/rgba 0 1 0 0.25))
(def blue     (col/rgba 0 0 1 1.0))
(def lt-blue  (col/rgba 0 0 1 0.25))
(def grey  (col/rgba 0.1 0.1 0.1))
(def lt-grey  (col/rgba 0.1 0.1 0.1 0.25))
(def lt-purple (col/rgba 0.8 0 0.8 0.25))
(def clear    (col/rgba 0 0 0 0.0))

(defn c->css [color]
  @(col/as-css color))

(def color-table
  {:red (c->css red)
   :lt-red (c->css lt-red)
   :green (c->css green)
   :lt-green (c->css lt-green)
   :blue (c->css blue)
   :lt-blue (c->css lt-blue)
   :lt-purple (c->css lt-purple)
   :grey (c->css grey)
   :lt-grey (c->css lt-grey)
   :clear (c->css clear)})

(defn config-with-resolution [res]
  {:domain [-2 2]
   :range [-2 2]
   :resolution [res res]})

(def config-1 (config-with-resolution 200))

(def round-pt (fn [p] (mapv Math.round p)))

(defn user->screen
  [config]
  (let [[xi xf] (:domain config)
        [yi yf] (:range config)
        [xres yres] (:resolution config)
        sx (/ xres (- xf xi))
        sy (/ yres (- yi yf))
        scale (v/scale sx sy)
        translate (v/translation [(- xi) (- yf)])]
    (fn [p]
      (if (number? p)
        (* sx p)
        ((comp round-pt scale translate n/coords) p)))))

(defn transform-fn [resolution]
  (let [config (config-with-resolution resolution)]
    (user->screen config)))

(def t-fn (transform-fn 200))

(comment
  (require '[turtle-graphics.transforms])
  (in-ns 'turtle-graphics.transforms)
  )
