(ns turtle-graphics.turtles.square.svg.components
  "reagent componets"
  (:require [turtle-graphics.core :as cp :refer [Command]]
            [turtle-graphics.transforms :as t]
            [turtle-graphics.turtles.square.svg.turtle :as turtle]
            [turtle-graphics.turtles.square.svg.programs :as programs]
            [complex.number :as n]
            [cljs.core.match :refer-macros [match]]
            [reagent.core :as reagent]
            [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [cljs.core.async.macros :refer [go]]))

(defn send!
  "Send information from the user to the message queue.
  The message must be a record which implements the Processor protocol."
  [channel message]
  (fn [dom-event]
    (put! channel message)
    (.stopPropagation dom-event)))

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
   [:button {:on-click (run-program! ui-channel programs/t-square 100)}
    "Square"]
   [:button {:on-click (run-program! ui-channel
                        (programs/two-step-circle :lt-blue :lt-purple)
                        100)}
    "Two Step"]
   [:button {:on-click (run-program! ui-channel
                        (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
                        100)}
    "Shell"]
   [:button {:on-click (run-program! ui-channel
                        (programs/root2-flower :lt-green :lt-blue :lt-red :lt-purple)
                        100)}
    "Root 2 flower"]
   ])
