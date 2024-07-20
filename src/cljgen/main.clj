(ns cljgen.main
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [selmer.parser])
  (:gen-class))


;;;; Utils

(defn- home-path
  "Return $HOME."
  []
  (System/getProperty "user.home"))

(defn- config-file-path
  "Return file in config dir."
  [& paths]
  (apply io/file (home-path) ".config" "cljgen" paths))

(defn- template-names
  "Return all template names."
  []
  (->> (config-file-path "templates")
       .list
       (filter #(not (string/starts-with? % ".")))
       (filter #(-> (config-file-path "templates" %) .isDirectory))))


;;;; Entrypoint

(defn -main
  "The entrypoint."
  [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"}))
  (println (template-names)))
