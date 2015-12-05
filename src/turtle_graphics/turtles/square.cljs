(ns turtle-graphics.turtles.square
  (:require
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(defcard-doc
  "
# Square Turtle

a complex svg turtle who lives in an svg element

## Turtle Commands
this turtle has a simple set of commands:

* move
* draw a line,
* make a point
* make a circle with a given style
* turn left or right by 90 degrees

commands are represented by vectors with keywords, in the style of hiccup:

* [:move d] where d is an integer
* [:penstroke color]
* [:penfill color]
* [:forward d]
* [:circle]
* [:point]
* [:left]
* [:right]

## Turtle Programs

a classical turtle program has hte form:

repeat 4
 [forward 50 right 90]

our programs make use of functions and vectors:
")
