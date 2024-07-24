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

(defn- config-file
  "Return file in config dir."
  [& paths]
  (apply fs/file (fs/home) ".config" "cljgen" paths))

(defn- template-names
  "Return all template names."
  []
  (let [template-dir (config-file "templates")]
    (->> template-dir
         file-seq
         (filter #(= ".cljgen.yml" (fs/file-name %)))
         (map #(str (fs/relativize template-dir (fs/parent %))))
         set)))

(defn- emit-template
  "Emit template."
  [template base-dir template-args]
  (let [template-dir (config-file "templates" template)]
    (doseq [template-file (file-seq template-dir)]
      (when (and (fs/regular-file? template-file)
                 (not (= ".cljgen.yml" (fs/file-name template-file))))
        (let [target-file (fs/file base-dir (fs/relativize template-dir template-file))
              target-file-dir (fs/parent target-file)]
          (when-not (fs/directory? target-file-dir)
            (log/info (format "Mkdir: %s" (str target-file-dir)))
            (fs/create-dirs target-file-dir))
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
