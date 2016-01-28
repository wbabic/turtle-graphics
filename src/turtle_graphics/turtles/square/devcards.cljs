(ns turtle-graphics.turtles.square.devcards
  "square turtle implementation"
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

(def app-state (reagent/atom turtle/initial-app-state))

(defn svg-command->string [command t-fn]
  (match command
         [:M p]
         (let [[px py] (t-fn p)]
           (str "M " px " " py " "))
         [:L p]
         (let [[px py] (t-fn p)]
           (str "L " px " " py " "))
         :else nil))

(defn svg-command->text [command]
  (match command
         [:M p]
         (let [[px py] (n/coords p)]
           (str "M " px " " py " "))
         [:L p]
         (let [[px py] (n/coords p)]
           (str "L " px " " py " "))
         :else nil))

(defn svg-path-string [svg-commands t-fn]
  (clojure.string/trim
   (clojure.string/join
    (map #(svg-command->string % t-fn) svg-commands))))

(defn svg-path-text [svg-commands]
  (clojure.string/trim
   (clojure.string/join
    (map svg-command->text svg-commands))))

(defn svg-circle [circle t-fn]
  (let [{:keys [stroke fill center radius]} circle
        [cx cy] (t-fn center)]
    [:circle {:stroke (t/color-table stroke)
              :fill (t/color-table fill)
              :cx cx
              :cy cy
              :r (t-fn radius)}]))

(defn svg-point [circle t-fn]
  (let [{:keys [stroke fill center radius]} circle
        [cx cy] (t-fn center)]
    [:circle {:stroke (t/color-table stroke)
              :fill (t/color-table fill)
              :cx cx
              :cy cy
              :r 3}]))

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
        path-string (svg-path-string svg-commands t-fn)]
    [:svg {:width resolution :height resolution}
     [:path {:d path-string
             :stroke "black" :fill "white"}]
     (into [:g {:className "circle-group"}] (map #(svg-circle % t-fn) circles))
     (into [:g {:className "point-group"}] (map #(svg-point % t-fn) points))]))



;; a turtle program execution environment consists of
;; a turtle-channel
;; and a go loop that listens for commands and then processes them
;; running a program consists of sending turtle commmands to the turtle-channel
(def turtle-channel (chan))

(go (loop []
      (let [command (<! turtle-channel)]
        (swap! app-state #(turtle/process-command command %))
        (recur))))

(defn run-program [turtle-program delay]
  (go
    (doseq [command turtle-program]
      (<! (timeout delay))
      (>! turtle-channel command))))

(defn run-program! [prog delay]
  (fn [] (run-program prog delay)))

(defn moves
  "square dance moves"
  []
  [:div
   [:button {:on-click (run-program! programs/t-square 100)}
    "Square"]
   [:button {:on-click (run-program!
                        (programs/two-step-circle :lt-blue :lt-purple)
                        100)}
    "Two Step"]
   [:button {:on-click (run-program!
                        (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
                        100)}
    "Shell"]
   [:button {:on-click (run-program!
                        (programs/root2-flower :lt-green :lt-blue :lt-red :lt-purple)
                        100)}
    "Root 2 flower"]
   [:button {:on-click #(reset! app-state turtle/initial-app-state)}
    "Reset"]])

(defn moves-gui []
  [:div
   (moves)])

(defn render-turtle-component [app-state]
  (let [app @app-state
        t (get-in app [:turtle])
        h (:heading t)
        pos (:position t)
        p (get-in app [:svg :path])
        circles (get-in app [:svg :circles])
        points (get-in app [:svg :points])]
    [:div
     [moves-gui]
     (render-svg app 200 t/t-fn)
     [:p (str "position: " (n/coords pos))]
     [:p (str "heading: " (n/coords h))]
     [:p (str "svg-path: " (svg-path-text p))]
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

  (run-program (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (programs/half-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (programs/quarter-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (programs/double-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (programs/quad-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (programs/root2-flower :lt-green :lt-blue :lt-red :lt-purple) 100)
  (run-program (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple) 100)

  (run-program
   (concat
    (programs/turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
    (programs/double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple)) 100)

  (run-program
   (concat
    (programs/quad-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/circle-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/half-dance :lt-green :lt-blue :lt-red :lt-purple)
    (programs/quarter-dance :lt-green :lt-blue :lt-red :lt-purple)) 10))
