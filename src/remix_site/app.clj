(ns remix-site.app
  (:use [ring.middleware [params :only [wrap-params]] [keyword-params :only [wrap-keyword-params]]
         [multipart-params :only [wrap-multipart-params]] [cookies :only [wrap-cookies]]
         [session :only [wrap-session]] [flash :only [wrap-flash]]]
        [compojure [core :only [defroutes GET]]
         [route :only [resources files]] [response :only [render]]]
        [remix.middleware.nested-params :only [wrap-nested-params]]
        [remix.rhandler :only [wrap-rhandler]]
        [remix-site.views.common :only [layout]]))

(defroutes loading-routes
  (resources "/")
  (fn [req] (render (layout
                    [:div.container
                     [:div.alert.alert-info "Site still loading. Please try again."]]) req)))

(defroutes routes
  (resources "/"))

(def app (-> routes
             (wrap-rhandler loading-routes "remix-site.views" "remix.slow")
             wrap-keyword-params
             wrap-nested-params
             wrap-params
             wrap-multipart-params
             wrap-flash
             wrap-session))