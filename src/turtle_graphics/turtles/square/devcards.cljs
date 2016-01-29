(ns turtle-graphics.turtles.square.devcards
  "square turtle implementation"
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

(def app-state (reagent/atom turtle/initial-app-state))

(defn text-circle [circle t-fn]
  (let [{:keys [stroke fill center radius]} circle]
    [fill (n/coords center) radius]))

(defn text-point [circle t-fn]
  (let [{:keys [stroke fill center radius]} circle]
    (n/coords center)))

(defn render-svg
  "create svg component for svg-commands"
  [app resolution t-fn]
  (let [svg-commands (get-in app [:svg :path])
        circles (get-in app [:svg :circles])
        points(get-in app [:svg :points])
        path-string (svg/svg-path-string svg-commands t-fn)
        turtle (:turtle app)
        position (:position turtle)
        endpoint (turtle/endpoint turtle)]
    [:svg {:width resolution :height resolution}
     [:path {:d path-string
             :stroke "black" :fill "white"}]
     (into [:g {:className "circle-group"}] (map #(svg/svg-circle % t-fn) circles))
     (into [:g {:className "point-group"}] (map #(svg/svg-point2 % t-fn) points))
     (svg/turtle->svg position endpoint t-fn)]))

;; a turtle program execution environment consists of
;; a turtle-channel
;; and a go loop that listens for commands and then processes them
;; running a program consists of sending turtle commmands to the turtle-channel
(def turtle-channel (chan))

(go (loop []
      (let [command (<! turtle-channel)]
        (swap! app-state #(turtle/process-command command %))
        (recur))))

(defn reset-button
  []
  [:div
   [:button {:on-click #(reset! app-state turtle/initial-app-state)}
    "Reset"]])

(defn moves-gui []
  [:div
   (c/moves turtle-channel)])

(defn render-turtle-component [app-state]
  (let [app @app-state
        t (get-in app [:turtle])
        h (:heading t)
        pos (:position t)
        p (get-in app [:svg :path])
        circles (get-in app [:svg :circles])
        points (get-in app [:svg :points])]
    [:div
     [c/command-buttons-comp turtle-channel]
     [moves-gui]
     [reset-button]
     (render-svg app 200 t/t-fn)
     [:p (str "position: " (n/coords pos))]
     [:p (str "heading: " (n/coords h))]
     [:p (str "svg-path: " (svg/svg-path-text p))]
     [:p (str "circles: "
              (clojure.string/join " "
                                   (map (comp str text-circle) circles)))]
     [:p (str "points: "
              (clojure.string/join " "
                                   (map (comp str text-point) points)))]]))

(defcard-rg render-turtle
  [render-turtle-component app-state]
  app-state)

(defcard-doc
  "### Square Turtle Programs"
  (dc/mkdn-pprint-source programs/t-square)
  (dc/mkdn-pprint-source programs/two-step-circle))

(comment
  (in-ns 'turtle-graphics.turtles.square.devcards)
  (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple)
  (keys @app-state)
  (c/->Forward 10)

  (c/run-program (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (c/run-program (programs/half-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (c/run-program (programs/quarter-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (c/run-program (programs/double-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (c/run-program (programs/quad-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (c/run-program (programs/root2-flower :lt-green :lt-blue :lt-red :lt-purple) 100)
  (c/run-program (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple) 100)

  (c/run-program
   (concat
    (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
    (programs/double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple)) 100)

  (c/run-program
   (concat
    (programs/quad-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/half-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/quarter-dance :lt-green :lt-blue :lt-red :lt-purple)) 10))
