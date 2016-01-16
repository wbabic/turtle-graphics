(ns turtle-graphics.turtles.square.programs
  "turtle square dances using square turtle commands"
  (:require [turtle-graphics.turtles.square.commands :as c]))

(def t-square
  (flatten (repeat 4 [(c/->Forward 1) (c/->Left)])))

(defn two-step-circle [c1 c2]
  (list (c/->Forward 1)
        (c/->Circle c1)
        (c/->Point :lt-grey)
        (c/->Move -2)
        (c/->Circle c2)
        (c/->Point :lt-grey)
        (c/->Forward 1)))

(defn circle-dance [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Point :lt-grey)
    (two-step-circle c1 c2)
    (c/->Left)
    (two-step-circle c3 c4)
    (c/->Right))))

(defn two-step-circle-no-lines [c1 c2]
  (list (c/->Move 1)
        (c/->Circle c1)
        (c/->Move -2)
        (c/->Circle c2)
        (c/->Move 1)))

(defn circle-dance-no-lines [c1 c2 c3 c4]
  (flatten
   (list
    (two-step-circle-no-lines c1 c2)
    (c/->Left)
    (two-step-circle-no-lines c3 c4)
    (c/->Right))))

(defn half-dance [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Resize (/ 2))
    (circle-dance-no-lines c1 c2 c3 c4)
    (c/->Resize 2))))

(defn quarter-dance [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Resize (/ 2))
    (c/->Resize (/ 2))
    (circle-dance-no-lines c1 c2 c3 c4)
    (c/->Resize 2)
    (c/->Resize 2))))

(defn double-dance [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Resize 2)
    (circle-dance c1 c2 c3 c4)
    (c/->Resize (/ 2)))))

(defn quad-dance [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Resize 2)
    (c/->Resize 2)
    (circle-dance c1 c2 c3 c4)
    (c/->Resize (/ 2))
    (c/->Resize (/ 2)))))

(defn root2-flower [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Circle :clear)
    (double-dance c1 c2 c3 c4)
    (circle-dance c1 c2 c3 c4)
    (half-dance c1 c2 c3 c4))))

(defn turtle-shell [c1 c2 c3 c4]
  (flatten
   (list
    (c/->Circle :clear)
    (half-dance c1 c2 c3 c4)
    (c/->Left) (c/->Left)
    (quarter-dance c1 c2 c3 c4)
    (c/->Left) (c/->Left))))

(comment
  (require '[turtle-graphics.turtles.square.programs])
  (in-ns 'turtle-graphics.turtles.square.programs)
  t-square
  (two-step-circle :red :blue)
  )
