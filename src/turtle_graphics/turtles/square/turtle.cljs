(ns turtle-graphics.turtles.square.turtle
  "square turtle implementation"
  (:require [complex.number :as n]
            [turtle-graphics.transforms :as t]
            [cljs.core.match :refer-macros [match]]
            [reagent.core :as reagent]
            [cljs.core.async :as async :refer [>! <! put! chan alts! timeout]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg defcard-doc]]
   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defrecord Square-turtle [position heading])

(def initial-turtle (->Square-turtle n/zero n/one))

(defn app-state-for-turtle
  [turtle]
  (let [position (:position turtle)]
    {:turtle turtle
     :svg {:path [[:M position]]
           :circles []
           :points []}}))

(def initial-app-state
  (app-state-for-turtle initial-turtle))

;; seven turtle commands
(defrecord Forward [d])
(defrecord Move [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Circle [color])
(defrecord Point [color])
(defrecord Resize [s])

;; turtle command processor
(defprotocol Command
  (process-command [command app-state]))

(extend-protocol Command
  Forward
  (process-command [{d :d} app-state]
    (let [heading (get-in app-state [:turtle :heading])
          position (get-in app-state [:turtle :position])
          v (n/mult heading d)
          w (n/add position v)]
      (-> app-state
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:L w])))))
  Move
  (process-command [{d :d} app-state]
    (let [heading (get-in app-state [:turtle :heading])
          position (get-in app-state [:turtle :position])
          v (n/mult heading d)
          w (n/add position v)]
      (-> app-state
          (update-in [:turtle :position] #(n/add % (n/mult heading d)))
          (update-in [:svg :path] #(conj % [:M w])))))
  Left
  (process-command [_ app-state]
    (update-in app-state [:turtle :heading] #(n/mult % n/i)))
  Right
  (process-command [_ app-state]
    (update-in app-state [:turtle :heading] #(n/mult % n/negative-i)))
  Circle
  (process-command [{color :color} app-state]
    (let [p (get-in app-state [:turtle :position])
          h (get-in app-state [:turtle :heading])
          r (n/length h)
          circle {:stroke :grey :fill color :center p :radius r}]
      (update-in app-state [:svg :circles] #(conj % circle))))
  Point
  (process-command [{color :color} app-state]
    (let [p (get-in app-state [:turtle :position])
          h (get-in app-state [:turtle :heading])
          r (n/length h)
          circle {:stroke :grey :fill color :center p}]
      (update-in app-state [:svg :points] #(conj % circle))))
  Resize
  (process-command [{s :s} app-state]
    (update-in app-state [:turtle :heading] #(n/mult % s))))

(comment
  (process-command (->Forward 1) initial-app-state))


(def app-state (reagent/atom initial-app-state))

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

(defn render-turtle-component [app-state]
  (let [app @app-state
        t (get-in app [:turtle])
        h (:heading t)
        pos (:position t)
        p (get-in app [:svg :path])
        circles (get-in app [:svg :circles])
        points (get-in app [:svg :points])]
    [:div
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
  "
## A rendering of application state

application state consists of

* a turtle with position and heading
* an svg path of lines and moves
* circles and points

there is

* a turtle channel
* a recurring go loop
* a command processer

a turtle command is put on the turtle channel
the go loop picks up the command and sends it to the comand processor
which updates the application state and
causes a rerendering of the page

use run-program, in this namespace,

```clojure
(run-program
   (concat
    (quad-dance :lt-green :lt-blue :lt-red :lt-purple)
    (double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (circle-dance :lt-green :lt-blue :lt-red :lt-purple)
    (half-dance :lt-green :lt-blue :lt-red :lt-purple)
    (quarter-dance :lt-green :lt-blue :lt-red :lt-purple)) 100)
```

to send a sequence of commands to the turtle channel
and watch the turtle program run

"
  [render-turtle-component app-state]
  app-state)


;; turtle dances

(def t-square
  (flatten (repeat 4 [(->Forward 1) (->Left)])))

(defn two-step-circle [c1 c2]
  (list (->Forward 1)
        (->Circle c1)
        (->Point :lt-grey)
        (->Move -2)
        (->Circle c2)
        (->Point :lt-grey)
        (->Forward 1)))

(defn circle-dance [c1 c2 c3 c4]
  (flatten
   (list
    (->Point :lt-grey)
    (two-step-circle c1 c2)
    (->Left)
    (two-step-circle c3 c4)
    (->Right))))

(defn two-step-circle-no-lines [c1 c2]
  (list (->Move 1)
        (->Circle c1)
        (->Move -2)
        (->Circle c2)
        (->Move 1)))

(defn circle-dance-no-lines [c1 c2 c3 c4]
  (flatten
   (list
    (two-step-circle-no-lines c1 c2)
    (->Left)
    (two-step-circle-no-lines c3 c4)
    (->Right))))

(defn half-dance [c1 c2 c3 c4]
  (flatten
   (list
    (->Resize (/ 2))
    (circle-dance-no-lines c1 c2 c3 c4)
    (->Resize 2))))

(defn quarter-dance [c1 c2 c3 c4]
  (flatten
   (list
    (->Resize (/ 2))
    (->Resize (/ 2))
    (circle-dance-no-lines c1 c2 c3 c4)
    (->Resize 2)
    (->Resize 2))))

(defn double-dance [c1 c2 c3 c4]
  (flatten
   (list
    (->Resize 2)
    (circle-dance c1 c2 c3 c4)
    (->Resize (/ 2)))))

(defn quad-dance [c1 c2 c3 c4]
  (flatten
   (list
    (->Resize 2)
    (->Resize 2)
    (circle-dance c1 c2 c3 c4)
    (->Resize (/ 2))
    (->Resize (/ 2)))))

(defn root2-flower [c1 c2 c3 c4]
  (flatten
   (list
    (->Circle :clear)
    (double-dance c1 c2 c3 c4)
    (circle-dance c1 c2 c3 c4)
    (half-dance c1 c2 c3 c4))))

(defn turtle-shell [c1 c2 c3 c4]
  (flatten
   (list
    (->Circle :clear)
    (half-dance c1 c2 c3 c4)
    (->Left) (->Left)
    (quarter-dance c1 c2 c3 c4)
    (->Left) (->Left))))

;; a turtle program execution environment consists of
;; a turtle-channel
;; and a go loop that listens for commands and then processes them
;; running a program consists of sending turtle commmands to the turtle-channel
(def turtle-channel (chan))

(go (loop []
      (let [msg (<! turtle-channel)]
        ;; (println msg)
        (swap! app-state #(process-command msg %))
        (recur))))

(defn run-program [turtle-program delay]
  (go
    (doseq [command turtle-program]
      (println command)
      (<! (timeout delay))
      (>! turtle-channel command))))

(comment
  (in-ns 'turtle-graphics.turtles.square.turtle)
  (run-program (circle-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (half-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (quarter-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (double-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (quad-dance :lt-green :lt-blue :lt-red :lt-purple) 1000)
  (run-program (root2-flower :lt-green :lt-blue :lt-red :lt-purple) 100)
  (run-program (turtle-shell :lt-green :lt-blue :lt-red :lt-purple) 100)

  (run-program
   (concat
    (turtle-shell :lt-green :lt-blue :lt-red :lt-purple)
    (double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (circle-dance :lt-green :lt-blue :lt-red :lt-purple)) 100)

  (run-program
   (concat
    (quad-dance :lt-green :lt-blue :lt-red :lt-purple)
    (double-dance :lt-green :lt-blue :lt-red :lt-purple)
    (circle-dance :lt-green :lt-blue :lt-red :lt-purple)
    (half-dance :lt-green :lt-blue :lt-red :lt-purple)
    (quarter-dance :lt-green :lt-blue :lt-red :lt-purple)) 10))
