(ns turtle-graphics.turtles.square.turtle
  "square turtle implementation"
  (:require [complex.number :as n]
            [cljs.core.match :refer-macros [match]]
            [reagent.core :as reagent]
            [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg defcard-doc]]
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defrecord Square-turtle [position heading])

(def initial-turtle (->Square-turtle n/zero n/one))

(def initial-app-state
  {:turtle initial-turtle
   :svg {:path []
         :circles []
         :points []}})

;; turtle commands
(defrecord Forward [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Circle [color])
(defrecord Point [color])

(defprotocol Command
  (process-command [command app]))

(extend-protocol Command
  Forward
  (process-command [{d :d} app]
    (let [heading (get-in app [:turtle :heading])]
      ;; update turtle
      ;; update svg path
      (-> app
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:l])))))
  Left
  (process-command [_ app]
    (update-in app [:turtle :heading] #(n/mult % n/i)))
  Right
  (process-command [_ app]
    (update-in app [:turtle :heading] #(n/mult % n/negative-i)))
  Circle
  (process-command [{color :color} app]
    (let [p (get-in app [:turtle :position])
          h (get-in app [:turtle :heading])
          r (n/length h)
          circle [:circle {:stroke :grey :fill color :center p :radius r}]]
      (update-in app [:svg :circles] #(conj % circle))))
  Point
  (process-command [{color :color} app]
    (let [p (get-in app [:turtle :position])
          h (get-in app [:turtle :heading])
          r (n/length h)
          circle [:point {:stroke :grey :fill color :center p}]]
      (update-in app [:svg :points] #(conj % circle)))))

(comment
  (process-command (->Forward 1) initial-app-state)
  )

(def app-state (reagent/atom initial-app-state))

(defn render-turtle-component [app-state]
  (let [app @app-state
        t (get-in app [:turtle])
        h (:heading t)
        pos (:position t)
        p (get-in app [:svg :path])
        circles (get-in app [:svg :circles])
        points (get-in app [:svg :points])]
    [:div
     [:p (str ":position" (n/coords pos))]
     [:p (str ":heading" (n/coords h))]
     [:p (str ":svg-path " p)]
     [:p (str ":circles " circles)]
     [:p (str ":points " points)]]))

(def turtle-channel (chan))

(defcard-rg render-turtle
  "
# Render Turtle
"
  [render-turtle-component app-state]
  app-state)

(go (loop []
      (let [msg (<! turtle-channel)]
        (println msg)
        (swap! app-state #(process-command msg %))
        (recur))))

(comment
  (go
    (>! turtle-channel :hello))
  (go
    (>! turtle-channel (->Forward 1)))
  )
