(ns remix-site.views.db
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-korma clj-snippet]]))

(defrh "/db" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "db" [:small " Remixed from " (link-to-korma)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/db.clj" "Machinery")
     " to manage database connections with " (link-to "http://www.mchange.com/projects/c3p0/" "c3p0") "."]]))