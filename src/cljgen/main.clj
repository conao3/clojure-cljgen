(ns cljgen.main
  (:require
   [selmer.parser])
  (:gen-class))

(defn -main [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"})))
