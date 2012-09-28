(ns remix-site.web
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [remix-site.app :as app]))

(defn -main []
  (app/prepare-app)
  (run-jetty #'app/app {:port (Integer. (or (System/getenv "PORT") "8080"))}))

; (-main)