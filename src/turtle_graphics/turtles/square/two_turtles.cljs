(ns turtle-graphics.turtles.square.two-turtles
  "a dance between two turtles"
  (:require [devcards.core]
            [turtle-graphics.core :as cp :refer [Command]]
            [turtle-graphics.transforms :as t]
            [turtle-graphics.turtles.square.turtle :as turtle]
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

(comment
  (in-ns 'turtle-graphics.turtles.square.two-turtles)
  )

(defn reduce-turtle [turtle & commands]
  (reduce (fn [t c] (turtle/process-command c t))
          turtle
          commands))

(def t0 turtle/init-state)
(def t1 (reduce-turtle t0
                       (turtle/->Forward -2)
                       (turtle/->Right)
                       (turtle/->Forward -2)
                       (turtle/->Resize (/ 2))))
(def t2 (reduce-turtle t0
                       (turtle/->Forward -2)
                       (turtle/->Right)
                       (turtle/->Forward 2)
                       (turtle/->Left)
                       (turtle/->Resize (/ 2))))

(def app-state (reagent/atom
                {:t1 t1
                 :t2 t2
                 :lines []}))

(comment
  (-> @app-state :t1 :turtle :position n/coords)
  )

(defn turtle->svg [turtle t-fn]
  (let [position (:position turtle)
        endpoint (turtle/endpoint turtle)]
    (svg/turtle->svg position endpoint t-fn)))

(defn process-channel [turtle-channel key]
  (go (loop []
        (let [command (<! turtle-channel)]
          (swap! app-state
                 #(update-in % [key] (fn [t] (turtle/process-command command t))))
          (recur)))))

;; add line between two turtles
(defn line [t1 t2]
  (let [p1 (:position t1)
        p2 (:position t2)]
    {:p1 p1 :p2 p2}))

(defn add-line []
  (let [app @app-state
        t1 (-> app :t1 :turtle)
        t2 (-> app :t2 :turtle)
        l (line t1 t2)]
    (swap! app-state #(update-in % [:lines] (fn [s] (conj s l))))))

(defn line-button
  []
  [:div
   [:button {:on-click #(add-line)}
    "Line from t1 to t2"]])

(defn command-buttons-comp
  "gui with command buttons"
  [ui-channel]
  [:div
   [:button {:on-click (c/send! ui-channel (turtle/->Forward 1))} "Forward"]
   [:button {:on-click (c/send! ui-channel (turtle/->Forward -1))} "Backward"]
   [:button {:on-click (c/send! ui-channel (turtle/->Left))} "Left"]
   [:button {:on-click (c/send! ui-channel (turtle/->Right))} "Right"]
   [:button {:on-click (c/send! ui-channel (turtle/->Resize (/ 2)))} "Half"]
   [:button {:on-click (c/send! ui-channel (turtle/->Resize 2))} "Double"]])

(defn svg-component [app-state]
  (let [t-chan-1 (chan)
        t-chan-2 (chan)
        app @app-state
        app-1 (:t1 app)
        app-2 (:t2 app)
        t-fn t/t-fn-2
        _ (process-channel t-chan-1 :t1)
        _ (process-channel t-chan-2 :t2)]
    [:div
     [:div
      [:p "turtle-1"]
      (command-buttons-comp t-chan-1)]
     [:div
      [:p "turtle-2"]
      (command-buttons-comp t-chan-2)]
     [line-button]
     [:svg {:width 400 :height 400}
      (svg/svg-lines app t-fn)
      (turtle->svg (:turtle app-1) t-fn)
      (turtle->svg (:turtle app-2) t-fn)]]))

(defcard-rg render-svg
  "A two turtle square dance"
  [svg-component app-state]
  app-state)
