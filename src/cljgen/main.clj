(ns cljgen.main
  (:require
   [babashka.cli :as cli]
   [clojure.java.io :as io]
   [clojure.string :as string]
   [selmer.parser])
  (:gen-class))

(set! *warn-on-reflection* true)

;;;; Utils

(defn- home-path
  "Return $HOME."
  []
  (System/getProperty "user.home"))

(defn- config-file-path
  "Return file in config dir."
  ^java.io.File
  [& paths]
  (apply io/file (home-path) ".config" "cljgen" paths))

(defn- template-names
  "Return all template names."
  []
  (->> (config-file-path "templates")
       .list
       (filter #(not (string/starts-with? % ".")))
       (filter #(-> (config-file-path "templates" %) .isDirectory))
       set))

;;;; Entrypoint

(def cli-spec
  {:spec
   {:template {:desc "Template name"}}})

(defn- get-help
  "Return help as string."
  [spec]
  (string/join
   "\n"
   ["cljgen [OPTIONS...]"
    ""
    "OPTIONS:"
    (cli/format-opts (assoc spec :order (keys (:spec spec))))]))

(defn- println-err
  "`println' but into stderr."
  [& args]
  (binding [*out* *err*]
    (apply println args)))

(defn -main
  "The entrypoint."
  [& _args]
  (println "hello")
  (println (selmer.parser/render "Hello {{name}}" {:name "Yogthos"}))
  (println (template-names))
  (println *command-line-args*)
  (let [opts (cli/parse-opts *command-line-args* cli-spec)
        {:keys [template]} opts
        template-candidates (template-names)]
    (when (or (:help opts) (:h opts))
      (println-err (get-help cli-spec))
      (System/exit 1))

    (println opts)
    (when-not (contains? template-candidates template)
      (println-err (format "%s is not defineded tempalte name.  Please specify one of %s"
                           template
                           template-candidates))
      (System/exit 1))))
