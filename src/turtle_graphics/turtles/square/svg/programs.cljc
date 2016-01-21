(ns turtle-graphics.turtles.square.svg.programs
  "turtle square dances using square turtle commands"
  (:require [turtle-graphics.turtles.square.svg.turtle :as turtle]))

(def t-square
  (flatten (repeat 4 [(turtle/->Forward 1) (turtle/->Left)])))

(defn two-step-circle [c1 c2]
  (list (turtle/->Forward 1)
        (turtle/->Circle c1)
        (turtle/->Point :lt-grey)
        (turtle/->Move -2)
        (turtle/->Circle c2)
        (turtle/->Point :lt-grey)
        (turtle/->Forward 1)))

(defn circle-dance [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Point :lt-grey)
    (two-step-circle c1 c2)
    (turtle/->Left)
    (two-step-circle c3 c4)
    (turtle/->Right))))

(defn two-step-circle-no-lines [c1 c2]
  (list (turtle/->Move 1)
        (turtle/->Circle c1)
        (turtle/->Move -2)
        (turtle/->Circle c2)
        (turtle/->Move 1)))

(defn circle-dance-no-lines [c1 c2 c3 c4]
  (flatten
   (list
    (two-step-circle-no-lines c1 c2)
    (turtle/->Left)
    (two-step-circle-no-lines c3 c4)
    (turtle/->Right))))

(defn half-dance [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Resize (/ 2))
    (circle-dance-no-lines c1 c2 c3 c4)
    (turtle/->Resize 2))))

(defn quarter-dance [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Resize (/ 2))
    (turtle/->Resize (/ 2))
    (circle-dance-no-lines c1 c2 c3 c4)
    (turtle/->Resize 2)
    (turtle/->Resize 2))))

(defn double-dance [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Resize 2)
    (circle-dance c1 c2 c3 c4)
    (turtle/->Resize (/ 2)))))

(defn quad-dance [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Resize 2)
    (turtle/->Resize 2)
    (circle-dance c1 c2 c3 c4)
    (turtle/->Resize (/ 2))
    (turtle/->Resize (/ 2)))))

(defn root2-flower [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Circle :clear)
    (double-dance c1 c2 c3 c4)
    (circle-dance c1 c2 c3 c4)
    (half-dance c1 c2 c3 c4))))

(defn turtle-shell [c1 c2 c3 c4]
  (flatten
   (list
    (turtle/->Circle :clear)
    (half-dance c1 c2 c3 c4)
    (turtle/->Left) (turtle/->Left)
    (quarter-dance c1 c2 c3 c4)
    (turtle/->Left) (turtle/->Left))))

(comment
  (require '[turtle-graphics.turtles.square.programs])
  (in-ns 'turtle-graphics.turtles.square.programs)
  t-square
  (two-step-circle :red :blue)
  )
