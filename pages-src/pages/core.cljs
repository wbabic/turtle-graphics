(ns pages.core
  (:require
   [devcards.core]
   [complex.number :as n :refer [zero one i negative-one negative-i infinity add sub mult div]]
   [turtle-graphics.intro]
   [turtle-graphics.turtles.square.devcards]
   [turtle-graphics.turtles.square.dance]
   [turtle-graphics.turtles.square.two-turtles]
   [turtle-graphics.turtles.simple.devcards])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg defcard-doc]]))

(devcards.core/start-devcard-ui!)
