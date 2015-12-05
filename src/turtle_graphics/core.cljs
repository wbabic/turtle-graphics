(ns turtle-graphics.core
  (:require
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-doc]]))

(enable-console-print!)

(defcard-doc
  "
# Turtle Graphics

## command execution environment

a simple turtle embedded into an svg element

with a set of colored pencils

that can stroke a line or fill a circle
"
)

(defn main []
  ;; conditionally start the app based on wether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
