(ns turtle-graphics.turtles.square.test
  (:require [turtle-graphics.turtles.square.commands :as c]
            [turtle-graphics.turtles.square.programs :as p]
            [turtle-graphics.turtles.square.state :as s]
            [turtle-graphics.turtles.square.processor]))

(comment
  (require '[turtle-graphics.turtles.square.test] :reload)
  (in-ns 'turtle-graphics.turtles.square.test)

  (c/->Forward 1)
  ;;=> #turtle_graphics.turtles.square.commands.Forward{:d 1}

  p/t-square
  (p/two-step-circle :red :blue)

  s/initial-app-state
  )
