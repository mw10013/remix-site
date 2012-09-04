(ns heroku-sandbox.web
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [heroku-sandbox.app :as app]))

(defn -main []
  (let [port (Integer/parseInt (System/getenv "PORT"))]
    (run-jetty app/app {:port port})))

