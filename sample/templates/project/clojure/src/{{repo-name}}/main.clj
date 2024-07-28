(ns {{ repo-name }}.main
  (:require
   [clojure.tools.logging :as log])
  (:gen-class))

(defn -main
  "The entrypoint."
  [& args]
  (log/info args))
