(ns cljgen.main
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [selmer.parser])
  (:gen-class))


;;;; Utils

(defn- expand-path-home
  "Expand `~` with $HOME."
  [path]
  (if (string/starts-with? path "~")
    (string/replace-first path "~" (System/getProperty "user.home"))
    path))

(defn- expand-path
  "Expand file path."
  [path]
  (-> path expand-path-home io/file .getCanonicalPath))

(defn- directory-exists?
  "Predicate to return whether the directory exists or not."
  [path]
  (let [file (java.io.File. path)]
    (and (-> file .exists)
         (-> file .isDirectory))))

(defn- make-directory
  "Make directory with parents dir."
  [path]
  (-> path java.io.File. .mkdirs))

(defn- ensure-config-dir
  "Return config dir and create dir if it does not exists."
  []
  (let [path (expand-path "~/.config/cljgen")]
    (when-not (directory-exists? path)
      (make-directory path))
    path))


;;;; Entrypoint

(defn -main
  "The entrypoint."
  [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"}))
  (println (ensure-config-dir)))
