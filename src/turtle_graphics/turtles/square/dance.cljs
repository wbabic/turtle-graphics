(ns turtle-graphics.turtles.square.dance
  "square turtle dance"
  (:require [devcards.core]
            [turtle-graphics.core :as cp :refer [Command]]
            [turtle-graphics.transforms :as t]
            [turtle-graphics.turtles.square.svg.turtle :as turtle]
            [turtle-graphics.turtles.square.svg.components :as c]
            [turtle-graphics.svg :as svg]
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

(def app-state (reagent/atom turtle/initial-app-state))

;; a turtle channel processor that listens to the turtle channel
(defn process-channel [turtle-channel]
  (go (loop []
        (let [command (<! turtle-channel)]
          (println command)
          (swap! app-state #(turtle/process-command command %))
          (recur)))))

(defn reset-button
  []
  [:div
   [:button {:on-click #(reset! app-state turtle/initial-app-state)}
    "Reset"]])

(defn svg-component [app-state]
  (let [turtle-chan (chan)
        _ (process-channel turtle-chan)
        app @app-state
        turtle (:turtle app)
        position (:position turtle)
        endpoint (turtle/endpoint turtle)
        t-fn t/t-fn]
    [:div
     (c/command-buttons-comp turtle-chan)
     (c/moves turtle-chan)
     [reset-button]
     [:svg {:width 800 :height 800}
      (svg/svg-path app t-fn)
      (svg/svg-circles app t-fn)
      (svg/svg-points app t-fn)
      (svg/turtle->svg position endpoint t-fn)]]))

(defcard-rg render-svg
  "A square turtle dance"
  [svg-component app-state]
  app-state)
