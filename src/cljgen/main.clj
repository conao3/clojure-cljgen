(ns cljgen.main
  (:require
   [babashka.cli :as cli]
   [clojure.java.io :as io]
   [clojure.string :as string]
   [clojure.tools.logging :as logging]
   [selmer.parser :as selmer]
   [selmer.util])
  (:gen-class))

(set! *warn-on-reflection* true)

;;;; Selmer

(defn missing-value-fn [tag _context-map]
  (throw (Exception. (format "Missing value: %s"
                             (or (:tag-value tag) (:tag-name tag))))))

(selmer.util/set-missing-value-formatter! missing-value-fn)

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
  (let [template-dir (config-file-path "templates")
        template-dir-path (-> template-dir .toPath)]
    (->> template-dir
         file-seq
         (filter #(= ".cljgen.yml" (-> ^java.io.File % .getName)))
         (map #(-> template-dir-path
                   (.relativize (-> ^java.io.File % .getParentFile .toPath))
                   str))
         set)))

(defn- emit-template
  "Emit template."
  [template base-dir template-args]
  (let [template-dir (config-file-path "templates" template)
        template-dir-path (-> template-dir .toPath)]
    (doseq [^java.io.File template-file (file-seq template-dir)]
      (when (and (-> template-file .isFile)
                 (not (= ".cljgen.yml" (-> template-file .getName))))
        (let [template-path (-> template-file .toPath)
              relative-path (-> template-dir-path (.relativize template-path))
              target-file (io/file base-dir (str relative-path))
              target-file-dir (-> target-file .getParentFile)]
          (when-not (-> target-file-dir .isDirectory)
            (logging/info (format "Mkdir: %s" (str target-file-dir)))
            (-> target-file-dir .mkdirs))
          (logging/info (format "Write: %s" (str target-file)))
          (spit target-file (selmer/render (slurp template-file) template-args)))))))

;;;; Entrypoint

(def cli-spec
  {:spec
   {:template {:desc "Template name"}
    :change-dir {:desc "Expand directory (Default: current-directory)"
                 :alias :C}}
   :exec-args
   {:change-dir (System/getProperty "user.dir")}})

(defn- get-help
  "Return help as string."
  [spec]
  (string/join
   "\n"
   ["cljgen [OPTIONS...]"
    ""
    "OPTIONS:"
    (cli/format-opts (assoc spec :order (keys (:spec spec))))]))

(defn -main
  "The entrypoint."
  [& args]
  (let [opts (cli/parse-opts args cli-spec)
        {:keys [template change-dir]} opts
        template-candidates (template-names)]
    (when (or (:help opts) (:h opts))
      (println (get-help cli-spec))
      (System/exit 1))

    (when-not (contains? template-candidates template)
      (throw (Exception. (format "%s is not defined template name.  Please specify one of %s"
                                 template
                                 template-candidates))))

    (when template
      (emit-template template change-dir {}))))
