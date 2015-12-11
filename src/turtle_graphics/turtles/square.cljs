(ns turtle-graphics.turtles.square
  (:require
   [sablono.core :as sab :include-macros true]
   [turtle-graphics.turtles.square.programs :as p])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(defcard-doc
  "
# Square Turtle

a complex svg turtle who lives in an svg element that can

* make square
* render svg
* make integer lattice
* define square root of 2
* root 2 flower

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

* [:forward d] where d is an integer
* [:left]
* [:right]
* [:circle color]
* [:point]

")
