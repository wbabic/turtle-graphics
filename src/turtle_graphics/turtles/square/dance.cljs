(ns turtle-graphics.turtles.square.dance
  "square turtle dance"
  (:require [devcards.core]
            [turtle-graphics.core :as cp :refer [Command]]
            [turtle-graphics.transforms :as t]
            [turtle-graphics.turtles.square.svg.turtle :as turtle]
            [turtle-graphics.turtles.square.svg.components :as c]
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

(defn svg-component [app-state]
  (let [turtle-chan (chan)
        _ (process-channel turtle-chan)]
    [:div
     (c/command-buttons-comp turtle-chan)
     (c/moves turtle-chan)
     [:svg {:width 400 :height 400}
      ]]))

(defcard-rg render-svg
  "A square turtle dance"
  [svg-component app-state]
  app-state)
