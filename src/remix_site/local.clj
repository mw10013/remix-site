(ns remix-site.local
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [remix-site.app :as app]))

(defonce server (run-jetty #'app/app {:port 8080 :join? false}))