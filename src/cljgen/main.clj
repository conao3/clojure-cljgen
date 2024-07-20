(ns cljgen.main
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [selmer.parser])
  (:gen-class))

(defn- expand-path-home [path]
  (if (string/starts-with? path "~")
    (string/replace-first path "~" (System/getProperty "user.home"))
    path))

(defn- expand-path [path]
  (-> path expand-path-home io/file .getCanonicalPath))

(defn- directory-exists? [path]
  (let [file (java.io.File. path)]
    (and (-> file .exists)
         (-> file .isDirectory))))

(defn- make-directory [path]
  (-> path java.io.File. .mkdirs))

(defn- ensure-config-dir
  "Return config dir and create dir if it does not exists."
  []
  (let [path (expand-path "~/.config/cljgen")]
    (when-not (directory-exists? path)
      (make-directory path))
    path))

(defn -main [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"}))
  (println (ensure-config-dir)))
