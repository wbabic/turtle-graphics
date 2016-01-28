(ns turtle-graphics.turtles.square.svg.turtle
  "an square turtle implementation in svg using complex numbers"
  (:require [complex.number :as n]))

(defprotocol Command
  (process-command [command app-state]))

(defrecord Square-turtle [position heading])

(def initial-turtle (->Square-turtle n/zero n/one))

(defn endpoint [turtle]
  (let [{:keys [position heading]} turtle]
    (n/add position heading)))

(defn app-state-for-turtle
  [turtle]
  (let [position (:position turtle)]
    {:turtle turtle
     :svg {:path [[:M position]]
           :circles []
           :points []}}))

(def initial-app-state
  (app-state-for-turtle initial-turtle))

;; define seven turtle commands
(defrecord Forward [d])
(defrecord Move [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Circle [color])
(defrecord Point [color])
(defrecord Resize [s])

(extend-protocol Command
  Forward
  (process-command [{d :d} app-state]
    (let [heading (get-in app-state [:turtle :heading])
          position (get-in app-state [:turtle :position])
          v (n/mult heading d)
          w (n/add position v)]
      (-> app-state
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:L w])))))
  Move
  (process-command [{d :d} app-state]
    (let [heading (get-in app-state [:turtle :heading])
          position (get-in app-state [:turtle :position])
          v (n/mult heading d)
          w (n/add position v)]
      (-> app-state
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:M w])))))
  Left
  (process-command [_ app-state]
    (update-in app-state [:turtle :heading] #(n/mult % n/i)))
  Right
  (process-command [_ app-state]
    (update-in app-state [:turtle :heading] #(n/mult % n/negative-i)))
  Circle
  (process-command [{color :color} app-state]
    (let [p (get-in app-state [:turtle :position])
          h (get-in app-state [:turtle :heading])
          r (n/length h)
          circle {:stroke :grey :fill color :center p :radius r}]
      (update-in app-state [:svg :circles] #(conj % circle))))
  Point
  (process-command [{color :color} app-state]
    (let [p (get-in app-state [:turtle :position])
          h (get-in app-state [:turtle :heading])
          r (n/length h)
          circle {:stroke :grey :fill color :center p}]
      (update-in app-state [:svg :points] #(conj % circle))))
  Resize
  (process-command [{s :s} app-state]
    (update-in app-state [:turtle :heading] #(n/mult % s))))

(comment
  (require '[turtle-graphics.turtles.square.svg.turtle :as t])
  (in-ns 'turtle-graphics.turtles.square.svg.turtle)
  (->Forward 10)
  ;;=> #turtle-graphics.turtles.square.commands.Forward{:d 10}
  initial-turtle
  initial-app-state

  (process-command (->Forward 10) initial-app-state)
  )
