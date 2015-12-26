(ns turtle-graphics.core
  "Overiew of turtle graphics in clojurescript"
  (:require
   [sablono.core :as sab :include-macros true]
   [turtle-graphics.turtles.square]
   [turtle-graphics.turtles.square.turtle :as sq-t])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(enable-console-print!)

(defcard-doc
  "
# Turtle Graphics

an svg turtle

embedded in an html element

listening in on a turtle channel

waiting for turtle commands to prosess

## Turtle state
the state of the turtle consists of a position and a heading

{:position zero :heading one}

## Turtle behavior
this turtle has the following abilities:

* make a point
* move forward
* move forward making a line as you go
* turn left or right (by +/- 90 degrees)
* make a circle of any color
* make a point of any color
* resize by half or double

commands are represented as data:

* (->Forward d) where d is an integer
* (->Move d) where d is an integer
* (->Left)
* (->Right)
* (->Circle color)
* (->Point color)
* (->Resize s) where s is scale factor of 1/2 or 2

## Turtle programs
a turtle program is a sequence of turtle commands,
a sequnce of data

which can be created using clojure functions

### Square Turtle Programs
"
  (dc/mkdn-pprint-source sq-t/t-square)
  (dc/mkdn-pprint-source sq-t/two-step-circle)
  (dc/mkdn-pprint-source sq-t/circle-dance)
  (dc/mkdn-pprint-source sq-t/half-dance)
  (dc/mkdn-pprint-source sq-t/root2-flower)

  "
## Turtle program execution
consists of sending turtle commands
to a turtle channel

with a listener that processes each command

by updating the app-state ratom

each update triggers a re-render

visit the 'turtle-graphics.turtles.square.turtle namespace

for a live working example where programs can be run from a repl

")

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

;; gallery

;; square-turtle
