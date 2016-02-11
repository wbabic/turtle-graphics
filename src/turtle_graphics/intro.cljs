(ns turtle-graphics.intro
  (:require
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(defcard-doc
  "
# Turtle Graphics
Here, the famous MIT turtle is reincarnated in spirit using the power of clojure and the reach of javascript.

We have a collection of programmable turtles living in various habitats starting with a simple turtle living in an svg element and displayable within the browser. There is also a turtle that lives in a 2d canvas, and some day, in a 3d context.

Each turtle has a state and a set of commands.
Each command implements the process-command method of the Processor protocol.
The process-command method takes a command and the state and returns a new transitioned state.

## Turtles
* [A simple turtle](#!/turtle_graphics.turtles.simple.devcards)
* [A square turtle](#!/turtle_graphics.turtles.square.devcards)

## Turtle state
The state of the turtle consists of a position in the plane and a heading vector,
like so:

{:position [0 0] :heading {:length 1 :angle 0}}

## Turtle commands
The simple turtle understands the following commands:

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

Other turtles may have additional commands.

## Turtle command processor
The turtle state is transformed in some way by the turtle commands.
The command processor listens for turtle commands placed on it's turtle channel.
Upon receiving a command the atom is swaped for the new transformed state.

## Turtle programs
A turtle program is a sequence of turtle commands,
which can be created using clojure functions.

## Turtle program execution
Each turtle has a program execution environment which involves sending turtle commands
to a turtle channel that has a listener that processes each command received
by updating the app-state Reagent atom,
triggering a re-render of the gui components.

For a live working example where programs can be run from a repl
visit the'turtle-graphics.turtles.square.turtle namespace.

")
