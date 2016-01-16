(ns turtle-graphics.turtles.square.state
  "svg app state with a square turtle"
  (:require [complex.number :as n]))

(defrecord Square-turtle [position heading])

(def initial-turtle (->Square-turtle n/zero n/one))

(defn app-state-for-turtle
  [turtle]
  (let [position (:position turtle)]
    {:turtle turtle
     :svg {:path [[:M position]]
           :circles []
           :points []}}))

(def initial-app-state
  (app-state-for-turtle initial-turtle))

(comment
  (require '[turtle-graphics.turtles.square.state] :reload)
  (in-ns 'turtle-graphics.turtles.square.state)
  initial-app-state
  )
