(ns remix-site.views.dbfn
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-korma link-to-jdbc link-to-remix clj-snippet]]))

(defrh "/dbfn" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "dbfn" [:small " Remixed from " (link-to-korma)]]]
    [:p (link-to-remix "dbfn.clj" "Machinery")
     " for database functions with " (link-to "/sql" "sql") " and " (link-to "/db" "db") "."]]))