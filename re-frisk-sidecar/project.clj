(defproject re-frisk-sidecar "0.5.10"
  :description "re-frisk remote debugger server"
  :url "https://github.com/flexsurfer/re-frisk"
  :license {:name "MIT License"
            :url "https://github.com/flexsurfer/re-frisk/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.0"]

                 [ring/ring-core "1.8.0"]
                 [ring-cors "0.1.8"]
                 [http-kit "2.3.0"]
                 [re-frisk/sente "1.15.0"]
                 [compojure "1.6.1"]
                 [com.cognitect/transit-clj "0.8.319"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/core.async "0.7.559"]

                 [org.clojure/clojurescript "1.9.671"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.1"]
                 [re-frisk-shell "0.5.2"]
                 [re-frisk "0.5.4"]
                 [day8.re-frame/re-frame-10x "0.3.2"]
                 [com.cognitect/transit-cljs "0.8.239"]]
  ;:main re-frisk-sidecar.core
  :plugins
  [[lein-cljsbuild      "1.1.4" :exclusions [[org.clojure/clojure]]]]

  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src"]
     :compiler {:main          re-frisk-sidecar.client
                :output-to     "resources/public/re-frisk.js"
                :optimizations :simple
                :pretty-print  false}}
    {:id "10x"
     :source-paths ["src"]
     :compiler {:main            re-frisk-sidecar.re-frame-10x
                :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                :output-to       "resources/public/10x.js"
                :optimizations   :simple
                :pretty-print    false}}]})
