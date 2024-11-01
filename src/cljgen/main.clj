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

(defn- template-names
  "Return all template names."
  [config-dir]
  (log/info (format "config-dir: %s" config-dir))
  (let [template-dir (fs/file config-dir "templates")]
    (->> template-dir
         file-seq
         (filter #(= ".cljgen.yml" (fs/file-name %)))
         (map #(str (fs/relativize template-dir (fs/parent %))))
         set)))

(defn- emit-template
  "Emit template."
  [template base-dir config-dir template-args]
  (let [template-dir (fs/file config-dir "templates" template)]
    (doseq [template-file (file-seq template-dir)]
      (when (and (fs/regular-file? template-file)
                 (not (= ".cljgen.yml" (fs/file-name template-file))))
        (let [target-file_ (fs/file base-dir (fs/relativize template-dir template-file))
              target-file (fs/file (selmer/render (str target-file_) template-args))]
          (fs/create-dirs (fs/parent target-file))
          (log/info (format "Write: %s" (str target-file)))
          (spit target-file (selmer/render (slurp template-file) template-args)))))))

;;;; Entrypoint

(def cli-spec
  {:restrict [:help :config-dir :template :change-dir]
   :spec
   {:help {:desc "Show help"
           :alias :h}
    :config-dir {:desc "Template dir"
                 :default-desc "<dir>"
                 :default (str (fs/file (fs/home) ".config" "cljgen"))}
    :template {:desc "Template name"}
    :change-dir {:desc "Expand directory (Default: current-directory)"
                 :alias :C
                 :default-desc "<dir>"
                 :default "."}}})

(defn- get-help
  "Return help as string."
  [spec]
  (string/join
   "\n"
   ["cljgen [OPTIONS...]"
    ""
    "OPTIONS:"
    (cli/format-opts (assoc spec :order (keys (:spec spec))))]))

(defn- error-and-exit
  "Emit error and exit."
  [msg]
  (log/error msg)
  (System/exit 1))

(defn -main
  "The entrypoint."
  [& raw-args]
  (let [{:keys [opts args] :as a} (cli/parse-args raw-args cli-spec)
        [cmd & args] args
        config-dir (if (fs/absolute? (:config-dir opts))
                     (:config-dir opts)
                     (str (fs/file (:config-dir opts))))]
    (log/info ["-main args" a])
    (when (:help opts)
      (println (get-help cli-spec))
      (System/exit 1))

    (case cmd
      "gen" (let [args (edn/read-string (first args))]
              (when-not (:template opts)
                (error-and-exit (str "Must specify --template argument.  opts: " opts)))
              (emit-template (:template opts) (:change-dir opts) config-dir args))
      "list" (doseq [elm (template-names config-dir)]
               (println elm))
      (log/error (format "`%s' is undefined command.  Please specify one of %s"
                         cmd ["gen" "list"])))))
