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
  (let [file (io/file path)]
    (and (-> file .exists)
         (-> file .isDirectory))))

(defn- ensure-dir
  "Make directory if it does not exists."
  [path]
  (when-not (directory-exists? path)
    (io/make-parents path))
  path)

(defn- config-dir-path
  "Return config dir."
  []
  (expand-path "~/.config/cljgen"))

(defn- template-dir-path
  "Return template dir."
  []
  (expand-path "~/.config/cljgen/template"))


;;;; Entrypoint

(defn -main
  "The entrypoint."
  [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"}))
  (println (ensure-dir (config-dir-path))))
