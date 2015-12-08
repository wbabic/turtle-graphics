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

a complex svg turtle who lives in an svg element that can

* make square
* render svg
* make integer lattice
* define square root of 2
* root 2 flower

## Turtle state
the state of the turtle consists of a position and a heading

{:position zero :heading one}

## Turtle style
the style of the turtle consits of two colors;
one for the stoke and one for the fill

## Turtle Commands
this turtle responds the following set of commands:

* make a point
* make a line
* turn left or right (by +/- 90 degrees)
* make a circle

commands are represented by vectors with keywords, in the style of hiccup:

* [:forward d] where d is an integer
* [:left]
* [:right]
* [:penstroke color]
* [:penfill color]
* [:circle]
* [:point]

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
