(ns leiningen.re-frisk
  (:require [leiningen.core.eval :as leval]))

(defn ^:no-project-needed re-frisk
  [project & [port]]
  (leval/eval-in-project
    {:dependencies '[[org.clojure/clojure "1.8.0"]
                     [re-frisk-sidecar "0.4.4"]]}
    `(re-frisk-sidecar.core/-main ~port)
    '(require 're-frisk-sidecar.core)))