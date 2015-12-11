(ns turtle-graphics.turtles.square.programs
  (:require [turtle-graphics.turtles.square.turtle :as sq-t]))

(def t-square
  (flatten (repeat 4 [(sq-t/->Forward 1) (sq-t/->Left)])))

(defn two-step-circle [c1 c2]
  (list (sq-t/->Forward 1)
        (sq-t/->Fill c1)
        (sq-t/->Circle)
        (sq-t/->Forward -2)
        (sq-t/->Fill c2)
        (sq-t/->Circle)
        (sq-t/->Forward 1)))

(defn circle-dance [c1 c2 c3 c4]
  (concat (two-step-circle c1 c2)
          (two-step-circle c3 c4)))
