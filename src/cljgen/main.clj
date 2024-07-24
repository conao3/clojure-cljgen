(ns cljgen.main
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [clojure.string :as string]
   [selmer.parser :as selmer]
   [selmer.util]
   [clojure.tools.logging :as log]
   [clojure.edn :as edn])
  (:gen-class))

(set! *warn-on-reflection* true)

;;;; Selmer

(defn missing-value-fn [tag _context-map]
  (throw (Exception. (format "Missing value: %s"
                             (or (:tag-value tag) (:tag-name tag))))))

(selmer.util/set-missing-value-formatter! missing-value-fn)

;;;; Utils

(defn- config-file-path
  "Return file in config dir."
  ^java.io.File
  [& paths]
  (apply fs/file (fs/home) ".config" "cljgen" paths))

(defn- template-names
  "Return all template names."
  []
  (let [template-dir (config-file-path "templates")
        template-dir-path (fs/path template-dir)]
    (->> template-dir
         file-seq
         (filter #(= ".cljgen.yml" (-> ^java.io.File % fs/file-name)))
         (map #(-> template-dir-path
                   (fs/relativize (-> ^java.io.File % fs/parent fs/path))
                   str))
         set)))

(defn- emit-template
  "Emit template."
  [template base-dir template-args]
  (let [template-dir (config-file-path "templates" template)
        template-dir-path (-> template-dir fs/path)]
    (doseq [^java.io.File template-file (file-seq template-dir)]
      (when (and (-> template-file .isFile)
                 (not (= ".cljgen.yml" (-> template-file fs/file-name))))
        (let [template-path (-> template-file fs/path)
              relative-path (-> template-dir-path (fs/relativize template-path))
              target-file (fs/file base-dir (str relative-path))
              target-file-dir (-> target-file fs/parent)]
          (when-not (-> target-file-dir .isDirectory)
            (log/info (format "Mkdir: %s" (str target-file-dir)))
            (-> target-file-dir fs/create-dirs))
          (log/info (format "Write: %s" (str target-file)))
          (spit target-file (selmer/render (slurp template-file) template-args)))))))

;;;; Entrypoint

(def cli-spec
  {:restrict [:help :template :change-dir]
   :spec
   {:help {:desc "Show help"
           :alias :h}
    :template {:desc "Template name"
               :validate {:pred #(contains? (template-names) %)
                          :ex-msg #(format "Invalid template `%s'.  Valid values: (%s)"
                                           % (string/join ", " (template-names)))}}
    :change-dir {:desc "Expand directory (Default: current-directory)"
                 :alias :C
                 :default-desc "<dir>"
                 :default (str (fs/cwd))}}})

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
  [& raw-args]
  (let [{:keys [opts args]} (cli/parse-args raw-args cli-spec)
        _ (when-not (= 1 (count args))
            (log/error "Must specify 1 argument only")
            (System/exit 1))
        args (edn/read-string (first args))]
    (log/info opts args)

    (when (:help opts)
      (println (get-help cli-spec))
      (System/exit 1))

    (when (:template opts)
      (emit-template (:template opts) (:change-dir opts) args))))
