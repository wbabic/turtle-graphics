(ns turtle-graphics.devcards
  (:require
   [sablono.core :as sab :include-macros true]
   [turtle-graphics.turtles.square.svg.programs :as p]
   [turtle-graphics.intro]
   [turtle-graphics.turtles.square.devcards]
   [turtle-graphics.turtles.square.dance]
   [turtle-graphics.turtles.square.two-turtles]
   [turtle-graphics.turtles.simple.devcards]
   [devcards.core])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)
