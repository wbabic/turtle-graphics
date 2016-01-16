(ns user
  (:require [figwheel-sidecar.repl-api :as ra]))

(def figwheel-config
  {:figwheel-options {:server-port 3451}
   ;; builds to focus on
   :build-ids        [ "cards" ]
   ;; load build configs from project file
   :all-builds       (figwheel-sidecar.config/get-project-builds)
   })

(defn start-dev
  "Start Figwheel and fw repl running this namespace from clojure.main"
  []
  (ra/start-figwheel! figwheel-config)
  (ra/cljs-repl))

(start-dev)
