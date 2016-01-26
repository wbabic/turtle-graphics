(ns turtle-graphics.devcards
  (:require
   [sablono.core :as sab :include-macros true]
   [turtle-graphics.turtles.square.svg.programs :as p]
   [turtle-graphics.turtles.square.devcards]
   [turtle-graphics.turtles.simple.devcards]
   [devcards.core])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(defcard-doc
  "
# Turtle Graphics

* a simple turtle in an svg context
* embedded in an html element
* living in a web page
* listening in on a turtle channel
* waiting for turtle commands to process

## Turtles
* [A simple turtle](#!/turtle_graphics.turtles.simple.devcards)
* [A square turtle](#!/turtle_graphics.turtles.square.devcards)

## Turtle state
The state of the turtle consists of a position in the plane and a heading vector,
like so:

{:position [0 0] :heading {:length 1 :angle 0}}

## Turtle commands
This turtle understands the following commands:

* move forward
* turn left by 90 degrees
* turn right by 90 degrees
* resize by half or double

which are represented as data records:

* (->Forward d) where d is an integer
* (->Left)
* (->Right)
* (->Resize s) where s is scale factor of 1/2 or 2

Each command implements the method

(process-command [command state])

of the Processor protocol.

## Turtle command processor
The turtle state is transformed in some way by the turtle commands.
The command processor listens for turtle commands placed on it's turtle channel.
Upon receiving a command the atom is swaped for the new transformed state.

## Turtle programs
a turtle program is a sequence of turtle commands,
a sequnce of data

which can be created using clojure functions

### Square Turtle Programs
"
  (dc/mkdn-pprint-source p/t-square)
  (dc/mkdn-pprint-source p/two-step-circle)
  (dc/mkdn-pprint-source p/circle-dance)
  (dc/mkdn-pprint-source p/half-dance)
  (dc/mkdn-pprint-source p/root2-flower)

  "
## Turtle program execution
consists of sending turtle commands
to a turtle channel

with a listener that processes each command

by updating the app-state ratom

each update triggers a re-render

visit the 'turtle-graphics.turtles.square.turtle namespace

for a live working example where programs can be run from a repl

")

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)
