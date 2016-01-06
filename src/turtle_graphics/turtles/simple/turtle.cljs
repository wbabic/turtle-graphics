(ns turtle-graphics.turtles.simple.turtle
  "square turtle implementation"
  (:require [complex.number :as n]
            [turtle-graphics.transforms :as t]
            [cljs.core.match :refer-macros [match]]
            [reagent.core :as reagent]
            [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg defcard-doc]]
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

;; seven turtle commands
(defrecord Forward [d])
(defrecord Move [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Circle [color])
(defrecord Point [color])
(defrecord Resize [s])

(defrecord Simple-turtle [position heading])

(def initial-turtle (->Simple-turtle n/zero n/one))

;; turtle command processor
(defprotocol Command
  (transform-turtle [command turtle]))

(extend-protocol Command
  Forward
  (transform-turtle [{d :d} turtle]
    (let [heading (:heading turtle)
          position (:position turtle)
          v (n/mult heading d)
          w (n/add position v)]
      (-> turtle
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:L w])))))
  Move
  (transform-turtle [{d :d} turtle]
    (let [heading (:heading turtle)
          position (:position turtle)
          v (n/mult heading d)
          w (n/add position v)]
      (-> turtle
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:M w])))))
  Left
  (transform-turtle [_ turtle]
    (update-in turtle [:turtle :heading] #(n/mult % n/i)))
  Right
  (transform-turtle [_ turtle]
    (update-in turtle [:turtle :heading] #(n/mult % n/negative-i)))
  Circle
  (transform-turtle [{color :color} turtle]
    (let [p (:position turtle)
          h (:heading turtle)
          r (n/length h)
          circle {:stroke :grey :fill color :center p :radius r}]
      (update-in turtle [:svg :circles] #(conj % circle))))
  Point
  (transform-turtle [{color :color} turtle]
    (let [p (:position turtle)
          h (:heading turtle)
          r (n/length h)
          circle {:stroke :grey :fill color :center p}]
      (update-in turtle [:svg :points] #(conj % circle))))
  Resize
  (transform-turtle [{s :s} turtle]
    (update-in turtle [:turtle :heading] #(n/mult % s))))

(defn two-step [c1 c2]
  (list (->Move 1)
        (->Circle c1)
        (->Move -2)
        (->Circle c2)
        (->Move 1)))

(defn circle-dance [c1 c2 c3 c4]
  (flatten
   (list
    (two-step c1 c2)
    (->Left)
    (two-step c3 c4)
    (->Right))))

(defn half-dance [c1 c2 c3 c4]
  (flatten
   (list
    (->Resize (/ 2))
    (circle-dance c1 c2 c3 c4)
    (->Resize 2))))

(defn turtle-shell [c1 c2 c3 c4]
  (flatten
   (list
    (->Circle :clear)
    (half-dance c1 c2 c3 c4))))

(comment
  (in-ns 'turtle-graphics.turtles.simple.turtle)

  )

(defcard-rg render-turtle
  "render a simple turtle ...")
