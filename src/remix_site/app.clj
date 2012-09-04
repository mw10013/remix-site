(ns remix-site.app
  (:use [compojure [core :only [defroutes GET]]
         [route :only [resources files]] [handler :only [site]]]
        [remix.rhandler :only [wrap-rhandler]]))

(defroutes routes
  (resources "/"))

(def app (-> routes (wrap-rhandler 'remix-site.views) site))