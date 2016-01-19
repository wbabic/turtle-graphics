(ns turtle-graphics.turtles.simple.core
  "documentation and a view of a simple turtle"
  (:require
   [turtle-graphics.turtles.simple.turtle :as t]
   [devcards.core]
   [reagent.core :as reagent])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc defcard-rg]]))

(comment
  (in-ns 'turtle-graphics.turtles.simple.core)
  )

(defcard-doc
  "
# A view of a simple turtle

Using devcards.

Consisting of

* turtle state text view
* turtle state rendered to svg
* a turtle channel to put commands onto
* gui components to put turtle commands onto a turtle channel
* a turtle command processor that processes turtle commands from a turtle channel
")

(def init-state
  {:turtle {:position [300 300] :heading {:angle 0 :length 100}}})

(def app-state (reagent/atom init-state))

(defn render-turtle-as-data [app-state]
  (let [app @app-state
        position (get-in app [:turtle :position])
        heading (get-in app [:turtle :heading])
        {:keys [length angle]} heading]
    [:div
     [:p (str "position: " position)]
     [:p (str "heading: " heading)]]))

(defcard-rg render-turtle-data
  "A reagent devcard to display turtle state as data."
  [render-turtle-as-data app-state]
  app-state)

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

(defn endpoint [turtle]
  (let [[x y] (:position turtle)
        {:keys [length angle]} (:heading turtle)]
    [(+ x (* length (Math/cos (t/deg->rad angle))))
     (+ y (* length (Math/sin (t/deg->rad angle))))]))

(defn render-turtle-as-svg [app-state]
  (let [app @app-state
        {:keys [position heading]} (:turtle app)]
    [:div
     [:svg {:width 600 :height 600}
      (svg-point position)]]))

(defcard-rg render-turtle-svg
  "A reagent devcard to display turtle state as svg."
  [render-turtle-as-svg app-state]
  app-state)
