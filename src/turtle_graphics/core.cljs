(ns turtle-graphics.core
  (:require
   [sablono.core :as sab :include-macros true]
   [turtle-graphics.turtles.square]
   [turtle-graphics.turtles.square.turtle])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(enable-console-print!)

(defcard-doc
  "
# Turtle Graphics

## a command execution environment

with an svg turtle embedded into an html element

listening in on a turtle channel

waiting for turtle commands
")

(defcard-doc
  "
# Square Turtle

an svg turtle who lives in an svg element that can

* make square
* render svg
* make integer lattice
* make root 2 flower

## Turtle state
the state of the turtle consists of a position and a heading

{:position zero :heading one}

## Turtle behavior
this turtle has the following abilities:

* make a point
* make a line
* turn left or right (by +/- 90 degrees)
* make a circle of any color

commands include:

* (->Forward d) where d is an integer
* (->Move d) where d is an integer
* (->Left)
* (->Right)
* (->Circle color)
* (->Point color)
* (->Resize s) where s is scale factor of 1/2 or 2

")

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
