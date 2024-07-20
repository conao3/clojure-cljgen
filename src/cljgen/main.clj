(ns cljgen.main
  (:require
   [clojure.java.io :as io]
   [selmer.parser])
  (:gen-class))


;;;; Utils

(defn- get-home
  "Return $HOME."
  []
  (System/getProperty "user.home"))

(defn- ensure-dir
  "Return dir and make directory if it does not exists."
  [dir]
  (doto dir
    (#(when-not (-> % .isDirectory)
        (-> % .mkdirs)))))

(defn- config-file
  "Return file in config dir."
  [& paths]
  (apply io/file (get-home) ".config" "cljgen" paths))

(defn- template-dir
  "Return template dir."
  []
  (config-file "template"))


;;;; Entrypoint

(defn -main
  "The entrypoint."
  [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"}))
  (println (ensure-dir (template-dir))))
