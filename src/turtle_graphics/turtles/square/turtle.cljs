(ns turtle-graphics.turtles.square.turtle
  "square turtle implementation"
  (:require [complex.number :as n]
            [cljs.core.match :refer-macros [match]]))

(enable-console-print!)

(defrecord Square-turtle [position heading])

(def initial-turtle (->Square-turtle n/zero n/one))

(def initial-app-state
  {:turtle {:geometry initial-turtle
            :style {:stroke :black :fill :white}}
   :svg {:path []
         :circles []
         :points []}})

;; turtle commands
(defrecord Forward [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Stroke [color])
(defrecord Fill [color])
(defrecord Circle [])
(defrecord Point [])

(defprotocol Command
  (process-command [command app]))

(extend-protocol Command
  Forward
  (process-command [{d :d} app]
    (let [heading (get-in app [:turtle :geometry :heading])]
      ;; update turtle
      (update-in app [:turtle :geometry :position] #(n/add % (n/mult heading d)))
      ;; update svg path
      (update-in app [:svg :path] #(conj % [:l]))))
  Left
  (process-command [_ app]
    (update-in app [:turtle :geometry :heading] #(n/mult % n/i)))
  Right
  (process-command [_ app]
    (update-in app [:turtle :geometry :heading] #(n/mult % n/negative-i)))
  Stroke
  (process-command [{color :color} app]
    (assoc-in app [:turtle :style :stroke] color))
  Fill
  (process-command [{color :color} app]
    (assoc-in app [:turtle :style :fill] color))
  Circle
  (process-command [_ app]
    (let [s (get-in app [:turtle :style :stroke])
          f (get-in app [:turtle :style :fill])
          p (get-in app [:turtle :geometry :position])
          h (get-in app [:turtle :geometry :heading])
          r (n/length h)
          circle [:circle {:stroke s :fill f :center p :radius r}]]
      (update-in app [:svg :circles] #(conj % circle))))
  Point
  (process-command [_ app]
    (let [s (get-in app [:turtle :style :stroke])
          f (get-in app [:turtle :style :fill])
          p (get-in app [:turtle :geometry :position])
          h (get-in app [:turtle :geometry :heading])
          r (n/length h)
          circle [:point {:stroke s :fill f :center p}]]
      (update-in app [:svg :points] #(conj % circle)))))

(comment
  (process-command (->Forward 1) initial-app-state)

 )
