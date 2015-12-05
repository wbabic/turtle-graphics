(ns turtle-graphics.turtles.square
  (:require
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(def square
  [:repeat 4 :forward :left])

(defn two-step [c1 c2]
  [[:forward 1]
   [:circle c1]
   [:forward -2]
   [:circle c2]
   [:forward 1]])

(defn square-dance [c1 c2 c3 c4]
  (concat (two-step c1 c2) [:left] (two-step c3 c4)))

(defcard-doc
  "
# Square Turtle

a complex svg turtle who lives in an svg element
with a simple set of commands

## Turtle state
the state of the turtle consists of a position and a heading

{:position zero :heading one}

## Turtle Commands
this turtle responds the following set of commands:

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
* [:circle style]
* [:point]
* [:left]
* [:right]

## Turtle Programs

### classical turtle program:

to square

repeat 4 [forward 50 right 90]

end

### clojurescript turtle programs

are made of vectors, keywords, functions and sequences:

```clojure
(def square
  [:repeat 4 :forward :left])

(defn two-step [c1 c2]
  [[:forward 1]
   [:circle c1]
   [:forward -2]
   [:circle c2]
   [:forward 1]])

(defn square-dance [c1 c2 c3 c4]
  (concat (two-step c1 c2) [:left] (two-step c3 c4)))

(square-dance :r :b :g :v)
;;=>
([:forward 1]
 [:circle :r]
 [:forward -2]
 [:circle :b]
 [:forward 1]
 :left
 [:forward 1]
 [:circle :g]
 [:forward -2]
 [:circle :v]
 [:forward 1])
```

")
