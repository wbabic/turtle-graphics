(ns turtle-graphics.turtles.simple.turtle
  "an implementation of a simple turtle")

;; command processor protocol
(defprotocol Processor
  "Command Processor"
  (process-command [command state] "process command, returning transitioned state"))

;; commands
(defrecord Forward [d])
(defrecord Left [])
(defrecord Right [])
(defrecord Resize [s])

;; state
(def init-state
  {:turtle {:position [0 0] :heading {:angle 0 :length 1}}})

(defn deg->rad [degrees]
  (/ (* degrees 2 Math/PI) 360))

(defn heading->vector [{:keys [angle length]}]
  (let [radians (deg->rad angle)]
    [(* length (Math/cos radians))
     (* length (Math/sin radians))]))

(extend-protocol Processor
  Forward
  (process-command [{d :d} state]
    (let [[hx hy] (heading->vector (get-in state [:turtle :heading]))
          [dx dy] [(* d hx) (* d hy)]]
      (update-in state [:turtle :position]
                 (fn [[x y]] [(+ x dx) (+ y dy)]))))
  Left
  (process-command [_ state]
    (update-in state [:turtle :heading :angle] #(+ % 90)))
  Right
  (process-command [_ state]
    (update-in state [:turtle :heading :angle] #(- % 90)))
  Resize
  (process-command [{s :s} state]
    (update-in state [:turtle :heading :length] #(* % s))))

(comment
  (require '[turtle-graphics.turtles.simple.turtle] :reload)
  (in-ns 'turtle-graphics.turtles.simple.turtle)
  (process-command (->Forward -10) init-state)
  ;;=> {:turtle {:position [-10 0], :heading {:angle 0, :length 1}}}
 )
