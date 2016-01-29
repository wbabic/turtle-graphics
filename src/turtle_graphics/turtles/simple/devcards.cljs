(ns turtle-graphics.turtles.simple.devcards
  "documentation and a view of a simple turtle"
  (:require
   [turtle-graphics.turtles.simple.turtle :as turtle]
   [turtle-graphics.transforms :as t]
   [turtle-graphics.svg :as svg]
   [devcards.core]
   [reagent.core :as reagent]
   [complex.vector :as v]
   [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc defcard-rg]]
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(comment
  (in-ns 'turtle-graphics.turtles.simple.devcards)
  (turtle/process-command (turtle/->Forward 1) init-state)
  )

(defcard-doc
  "
# A view of a simple turtle

This namespace include

* an app state consisting of a position and a heading in svg coordinates,
* views of the app state as data and svg,
* a turtle channel,
* command buttons that put turtle commands onto the turtle channel,
* a command processor that listens for commands, applying each command it receives, in turn,
to the current app state, transitioning to a new state.

The turtle itself is just a simple turtle defined in the turtle-graphics.turtles.simple.turtle namespace, in a cljc file, and is accessible via clojure and clojurescript repls and test environments. It responds to six commands: Forward and Backward, Left and Right, Half and Double; three pairs of inverses.

There are three interconnected devcards in this namespace, in addition to this one:

* A view of app state as data: [render-turtle-data](#!/turtle_graphics.turtles.simple.devcards/render-turtle-data)
* A view of app state as svg: [render-turtle-svg](#!/turtle_graphics.turtles.simple.devcards/render-turtle-svg)
* [command buttons](#!/turtle_graphics.turtles.simple.devcards/command-buttons)

This namespace also has a turtle channel and command processor:

* a turtle channel: #'turtle-graphics.turtles.square.devcards/turtle-channel
* a command processor function: #'turtle-graphics.turtles.simple.devcards/process-channel

Clicking a command button will send a turtle command to the turtle channel.
The command processor, upon receiving the command from the turtle channel, changes the app state.
The app state is actually a reagent atom and so the views are updated whenever the it changes. Try it out. Click a command button and see the state change in both views. Then read the code.
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
          (swap! app-state #(turtle/process-command command %))
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
  "A reagent devcard to display turtle state as data using screen coordinates."
  [render-turtle-as-data app-state]
  app-state)

(defn render-turtle-as-svg [app-state]
  (let [app @app-state
        turtle (:turtle app)
        endpoint (turtle/endpoint turtle)
        {:keys [position heading]} turtle]
    [:div
     [:svg {:width 400 :height 400}
      (svg/turtle->svg position endpoint identity)]]))

(defcard-rg render-turtle-svg
  "A reagent devcard to display turtle state as a 400 by 400 svg element."
  [render-turtle-as-svg app-state]
  app-state)

(defn send!
  "Send information from the user to the message queue.
  The message must be a record which implements the Processor protocol."
  [channel message]
  (fn [dom-event]
    (put! channel message)
    (.stopPropagation dom-event)))

(defn turtle-command-buttons []
  [:div
   [:button {:on-click (send! turtle-channel (turtle/->Forward 1))} "Forward"]
   [:button {:on-click (send! turtle-channel (turtle/->Forward -1))} "Backward"]
   [:button {:on-click (send! turtle-channel (turtle/->Right))} "Left"]
   [:button {:on-click (send! turtle-channel (turtle/->Left))} "Right"]
   [:button {:on-click (send! turtle-channel (turtle/->Resize (/ 2)))} "Half"]
   [:button {:on-click (send! turtle-channel (turtle/->Resize 2))} "Double"]])

(defcard command-buttons
  (reagent/as-element [turtle-command-buttons]))

(defn timer-component []
  (let [seconds-elapsed (reagent/atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      [:div
       "Seconds Elapsed: " @seconds-elapsed])))

(defcard-rg timer
  "a timer component"
  [timer-component])
