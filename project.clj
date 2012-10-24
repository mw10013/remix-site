(defproject org.clojars.mw10013/remix-site "0.0.1-SNAPSHOT"
  :description "Documentation site for remix"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring/ring "1.1.6"]
                 [clj-time "0.4.4"]
                 [org.clojars.mw10013/remix "0.0.6"]]
  :min-lein-version "2.0.0"
  :profiles {:dev {:dependencies [[org.hsqldb/hsqldb "2.2.8"]]}})
