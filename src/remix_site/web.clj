(ns remix-site.web
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [remix-site.app :as app]))

(defn -main []
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty app/app {:port port})))

