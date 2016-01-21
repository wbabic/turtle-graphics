(ns turtle-graphics.core)

;; turtle command processor protocol
(defprotocol Command
  (process-command [command app-state]))
