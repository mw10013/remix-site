(ns remix-site.app
  (:use [ring.middleware [params :only [wrap-params]] [keyword-params :only [wrap-keyword-params]]
         [multipart-params :only [wrap-multipart-params]] [cookies :only [wrap-cookies]]
         [session :only [wrap-session]] [flash :only [wrap-flash]]]
        [compojure [core :only [defroutes GET]]
         [route :only [resources files]] [handler :only [site]]]
        [remix.middleware.nested-params :only [wrap-nested-params]]
        [remix.rhandler :only [wrap-rhandler]]))

(defroutes routes
  (resources "/"))

(def app (-> routes
             (wrap-rhandler 'remix-site.views)
             wrap-keyword-params
             wrap-nested-params
             wrap-params
             wrap-multipart-params
             wrap-flash
             wrap-session))