(ns leiningen.re-frisk
  (:require [leiningen.core.eval :as leval]
            [clojure.data.json :as json]
            [clojure.string :as str]))

(defn- replace-host [txt host]
  (str/replace txt #":host \".*\"" (str ":host \"" host ":4567\"")))

(defn use-re-natal [project]
  (let [config (json/read-str (slurp ".re-natal") :key-fn keyword)
        dev-root (get-in config [:envRoots :dev])
        android-path (str dev-root "/env/android/main.cljs")
        ios-path (str dev-root "/env/ios/main.cljs")
        ios-host (get-in config [:platforms :ios :host] (:iosHost config))
        android-host (get-in config [:platforms :android :host] (:androidHost config))]
    (spit android-path (replace-host (slurp android-path) android-host))
    (spit ios-path (replace-host (slurp ios-path) ios-host))
    (leiningen.core.main/info (str "re-frisk server for iOS: " ios-host ":4567"))
    (leiningen.core.main/info (str "re-frisk server for Android: " android-host ":4567"))))

(defn ^:no-project-needed re-frisk
  {:subtasks [#'use-re-natal]}
  [project & [subtask-or-port]]
  (case subtask-or-port
    "use-re-natal" (use-re-natal project)
    (leval/eval-in-project
      {:dependencies '[[org.clojure/clojure "1.8.0"]
                       [re-frisk-sidecar "0.5.4"]]}
      `(re-frisk-sidecar.core/-main ~subtask-or-port)
      '(require 're-frisk-sidecar.core))))

