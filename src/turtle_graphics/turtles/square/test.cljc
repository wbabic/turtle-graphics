(ns turtle-graphics.turtles.square.test
  (:require [turtle-graphics.turtles.square.svg.turtle :as turtle]
            [turtle-graphics.turtles.square.svg.programs :as programs]))

(comment
  (require '[turtle-graphics.turtles.square.test] :reload)
  (in-ns 'turtle-graphics.turtles.square.test)

  (turtle/->Forward 1)
  ;;=> #turtle_graphics.turtles.square.commands.Forward{:d 1}

  programs/t-square
  (progams/two-step-circle :red :blue)

  turtle/initial-app-state
  )
