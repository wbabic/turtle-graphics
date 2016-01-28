(ns turtle-graphics.turtles.square.dance
  "square turtle dance"
  (:require [devcards.core]
            [turtle-graphics.core :as cp :refer [Command]]
            [turtle-graphics.transforms :as t]
            [turtle-graphics.turtles.square.svg.turtle :as turtle]
            [turtle-graphics.turtles.square.svg.programs :as programs]
            [complex.number :as n]
            [cljs.core.match :refer-macros [match]]
            [reagent.core :as reagent]
            [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg defcard-doc]]
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defcard-doc
  "
# Square turtle dance")

(comment
  (in-ns 'turtle-graphics.turtles.square.dance)
  )

;; an svg element with command buttons and program buttons
;; using an ui-channel to send turtle commands to the turtle processor

(defn send!
  "Send information from the user to the message queue.
  The message must be a record which implements the Processor protocol."
  [channel message]
  (fn [dom-event]
    (put! channel message)
    (.stopPropagation dom-event)))

(def app-state (reagent/atom turtle/initial-app-state))

;; a turtle channel processor that listens to the turtle channel
(defn process-channel [turtle-channel]
  (go (loop []
        (let [command (<! turtle-channel)]
          (println command)
          (swap! app-state #(turtle/process-command command %))
          (recur)))))

;; a designated turtle channel for this namespace
(def turtle-channel (chan))

(defn run-program [turtle-channel turtle-program delay]
  (go
    (doseq [command turtle-program]
      (<! (timeout delay))
      (>! turtle-channel command))))

(defn run-program! [channel program delay]
  (fn [] (run-program channel program delay)))

(defn command-buttons-comp
  "gui with command buttons"
  [ui-channel]
  [:div
   [:p "commands:"]
   [:button {:on-click (send! ui-channel (turtle/->Forward 1))} "Forward"]
   [:button {:on-click (send! ui-channel (turtle/->Forward -1))} "Backward"]
   [:button {:on-click (send! ui-channel (turtle/->Left))} "Left"]
   [:button {:on-click (send! ui-channel (turtle/->Right))} "Right"]
   [:button {:on-click (send! ui-channel (turtle/->Resize (/ 2)))} "Half"]
   [:button {:on-click (send! ui-channel (turtle/->Resize 2))} "Double"]])

(defn moves
  "square dance moves"
  [ui-channel]
  [:div
   [:p "moves:"]
   [:button {:on-click (run-program!
                        ui-channel
                        (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
                        100)}
    "Square"]
   [:button {:on-click (run-program!
                        ui-channel
                        (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
                        100)}
    "Shell"]])

(defn svg-component [app-state]
  (let [turtle-chan (chan)
        _ (process-channel turtle-chan)]
    [:div
     (command-buttons-comp turtle-chan)
     (moves turtle-chan)
     [:svg {:width 400 :height 400}
      ]]))

(defcard-rg render-svg
  "A square turtle dance"
  [svg-component app-state]
  app-state)
