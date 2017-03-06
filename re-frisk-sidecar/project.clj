(defproject re-frisk-sidecar "0.4.0"
  :description "re-frisk remote debugger server"
  :url "https://github.com/flexsurfer/re-frisk"
  :license {:name "MIT License"
            :url "https://github.com/flexsurfer/re-frisk/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring-cors "0.1.8"]
                 [ring/ring-defaults "0.1.5"]
                 [http-kit "2.2.0"]
                 [com.taoensso/sente "1.11.0"]
                 [compojure "1.5.2"]
                 [com.cognitect/transit-clj  "0.8.290"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/clojurescript "1.9.229"]
                 [org.clojure/core.async    "0.2.395"]
                 [reagent "0.6.0"]
                 [re-frame "0.8.0"]
                 [re-frisk-shell "0.1.0"]
                 [com.cognitect/transit-cljs "0.8.239"]]
  ;:main re-frisk-sidecar.core
  :plugins
  [[lein-cljsbuild      "1.1.4"]]

  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src"]
     :compiler {:main re-frisk-sidecar.client
                :output-to "resources/public/main.js"
                :optimizations :whitespace #_:advanced
                :pretty-print true}}
    {:id "min"
     :source-paths ["src"]
     :compiler {:output-to "resources/public/main.js"
                :main re-frisk-sidecar.client
                :optimizations :advanced
                :closure-defines {goog.DEBUG false}
                :pretty-print false}}]})