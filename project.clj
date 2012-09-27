(defproject org.clojars.mw10013/remix-site "0.0.1-SNAPSHOT"
  :description "Documentation site for remix."
  :url "https://github.com/mw10013/remix-site"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring/ring "1.1.3"]
                 [ring/ring-jetty-adapter "1.1.3"]
                 [clj-time "0.4.4"]
                 [org.clojars.mw10013/remix "0.0.5"]]
  :min-lein-version "2.0.0"
  :profiles {:dev {:dependencies [[org.hsqldb/hsqldb "2.2.8"]]}})