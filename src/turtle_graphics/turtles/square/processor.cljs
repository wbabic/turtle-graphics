(ns turtle-graphics.turtles.square.processor
  (:require [turtle-graphics.core :refer [Command]]
            [turtle-graphics.turtles.square.commands :as c]
            [turtle-graphics.turtles.square.state :as s]
            [complex.number :as n]))

(extend-protocol Command
  c/Forward
  (process-command [{d :d} app-state]
    (let [heading (get-in app-state [:turtle :heading])
          position (get-in app-state [:turtle :position])
          v (n/mult heading d)
          w (n/add position v)]
      (-> app-state
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:L w])))))
  c/Move
  (process-command [{d :d} app-state]
    (let [heading (get-in app-state [:turtle :heading])
          position (get-in app-state [:turtle :position])
          v (n/mult heading d)
          w (n/add position v)]
      (-> app-state
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:M w])))))
  c/Left
  (process-command [_ app-state]
    (update-in app-state [:turtle :heading] #(n/mult % n/i)))
  c/Right
  (process-command [_ app-state]
    (update-in app-state [:turtle :heading] #(n/mult % n/negative-i)))
  c/Circle
  (process-command [{color :color} app-state]
    (let [p (get-in app-state [:turtle :position])
          h (get-in app-state [:turtle :heading])
          r (n/length h)
          circle {:stroke :grey :fill color :center p :radius r}]
      (update-in app-state [:svg :circles] #(conj % circle))))
  c/Point
  (process-command [{color :color} app-state]
    (let [p (get-in app-state [:turtle :position])
          h (get-in app-state [:turtle :heading])
          r (n/length h)
          circle {:stroke :grey :fill color :center p}]
      (update-in app-state [:svg :points] #(conj % circle))))
  c/Resize
  (process-command [{s :s} app-state]
    (update-in app-state [:turtle :heading] #(n/mult % s))))

(comment
  (in-ns 'turtle-graphics.turtles.square.processor)
  (c/->Forward 10)
  ;;=> #turtle-graphics.turtles.square.commands.Forward{:d 10}
  s/initial-turtle


  )
