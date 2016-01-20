(ns turtle-graphics.turtles.simple.core
  "documentation and a view of a simple turtle"
  (:require
   [turtle-graphics.turtles.simple.turtle :as t]
   [devcards.core]
   [reagent.core :as reagent]
   [complex.vector :as v]
   [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc defcard-rg]]
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(comment
  (in-ns 'turtle-graphics.turtles.simple.core)
  (t/process-command (t/->Forward 1) init-state)
  )

(defcard-doc
  "
# A view of a simple turtle

Using devcards and reagent, consisting of

* turtle state text view
* turtle state rendered to svg
* a turtle channel to put commands onto
* gui components to put turtle commands onto a turtle channel
* a turtle command processor that processes turtle commands from a turtle channel
")

(def init-state
  {:turtle {:position [200 200] :heading {:angle 0 :length 100}}})

(def app-state (reagent/atom init-state))

;; a turtle channnel to put turtle commands onto
(def turtle-channel (chan))

;; a turtle channel processor that listens to the turtle channel
(defn process-channel [turtle-channel]
  (go (loop []
        (let [command (<! turtle-channel)]
          (swap! app-state #(t/process-command command %))
          (recur)))))

(process-channel turtle-channel)

(defn render-turtle-as-data [app-state]
  (let [app @app-state
        position (get-in app [:turtle :position])
        heading (get-in app [:turtle :heading])
        {:keys [length angle]} heading]
    [:div
     [:p (str "position: " position)]
     [:p (str "heading: " heading)]]))

(defcard-rg render-turtle-data
  "A reagent devcard to display turtle state as data."
  [render-turtle-as-data app-state]
  app-state)

(def point-options {:stroke "grey" :fill "red"})

(defn svg-point
  ([p] (svg-point p point-options))
  ([[x y] options]
   (let [{:keys [stroke fill]} options]
     [:circle {:stroke stroke
               :fill fill
               :cx x
               :cy y
               :r 3}])))

(defn svg-line
  ([p1 p2] (svg-line p1 p2 {:stroke "black"}))
  ([p1 p2 {:keys [stroke]}]
   (let [[x1 y1] p1
         [x2 y2] p2]
     [:line {:x1 x1 :x2 x2 :y1 y1 :y2 y2 :stroke stroke}])))

(defn endpoint [turtle]
  (let [[x y] (:position turtle)
        {:keys [length angle]} (:heading turtle)]
    [(+ x (* length (Math/cos (t/deg->rad angle))))
     (+ y (* length (Math/sin (t/deg->rad angle))))]))

(defn minus [v w]
  (v/sum v (v/scal-mul -1 w)))

(defn normalize [v]
  (v/scal-mul (/ (v/len v)) v))

(defn arrow-tips [position endpoint]
  (let [v (minus endpoint position)
        v1 (v/scal-mul 10 (normalize v))
        [vx vy] v1
        vp1 [(- vy) vx]
        vp2 [(- vy) (- vx)]
        av1 (minus vp1 v1)
        av2 (minus vp2 v1)]
    [(v/sum endpoint av1)
     (v/sum endpoint av2)]))

(defn render-turtle-as-svg [app-state]
  (let [app @app-state
        turtle (:turtle app)
        endpoint (endpoint turtle)
        {:keys [position heading]} turtle
        [at1 at2] (arrow-tips position endpoint)]
    [:div
     [:svg {:width 400 :height 400}
      (svg-point position)
      (svg-line position endpoint)
      (svg-line endpoint at1) (svg-line endpoint at2)]]))

(defcard-rg render-turtle-svg
  "A reagent devcard to display turtle state as svg."
  [render-turtle-as-svg app-state]
  app-state)

(defn send!
  "Send information from the user to the message queue.
  The message must be a record which implements the Message protocol."
  [channel message]
  (fn [dom-event]
    (put! channel message)
    (.stopPropagation dom-event)))

(defn turtle-command-buttons []
  [:div
   [:button {:on-click (send! turtle-channel (t/->Forward 1))} "Forward"]
   [:button {:on-click (send! turtle-channel (t/->Forward -1))} "Backward"]
   [:button {:on-click (send! turtle-channel (t/->Right))} "Left"]
   [:button {:on-click (send! turtle-channel (t/->Left))} "Right"]
   [:button {:on-click (send! turtle-channel (t/->Resize (/ 2)))} "Half"]
   [:button {:on-click (send! turtle-channel (t/->Resize 2))} "Double"]])

(defcard command-buttons
  (reagent/as-element [turtle-command-buttons]))
