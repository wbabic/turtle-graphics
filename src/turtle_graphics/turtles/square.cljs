(ns turtle-graphics.turtles.square
  "square turtle dances"
  (:require
   [sablono.core :as sab :include-macros true]
   [turtle-graphics.turtles.square.turtle :as sq-t])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

;; a component to run programs
