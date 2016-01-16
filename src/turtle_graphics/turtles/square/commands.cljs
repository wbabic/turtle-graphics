(ns turtle-graphics.turtles.square.commands)

;; define seven turtle commands
(defrecord Forward [d])
(defrecord Move [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Circle [color])
(defrecord Point [color])
(defrecord Resize [s])

(comment
  (require '[turtle-graphics.turtles.square.commands] :reload)
  (in-ns 'turtle-graphics.turtles.square.commands)

  (->Forward 10)
  ;;=> #turtle_graphics.turtles.square.commands.Forward{:d 10}
  )
