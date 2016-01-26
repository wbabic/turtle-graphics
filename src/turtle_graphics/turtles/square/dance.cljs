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


;; an svg element with command buttons and program buttons
;; using an ui-channel to send turtle commands to the turtle processor

(defn send!
  "Send information from the user to the message queue.
  The message must be a record which implements the Processor protocol."
  [channel message]
  (fn [dom-event]
    (put! channel message)
    (.stopPropagation dom-event)))

;; a designated turtle channel for this namespace
(def ui-channel (chan))

(defn command-buttons
  ""
  []
  (fn []
    [:div
     [:button {:on-click (send! ui-channel (turtle/->Forward 1))} "Forward"]
     [:button {:on-click (send! ui-channel (turtle/->Forward -1))} "Backward"]
     [:button {:on-click (send! ui-channel (turtle/->Right))} "Left"]
     [:button {:on-click (send! ui-channel (turtle/->Left))} "Right"]
     [:button {:on-click (send! ui-channel (turtle/->Resize (/ 2)))} "Half"]
     [:button {:on-click (send! ui-channel (turtle/->Resize 2))} "Double"]]))
