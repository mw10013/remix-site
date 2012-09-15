(ns remix-site.views.sql
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-mybatis link-to-jdbc clj-snippet]]))

(defrh "/sql" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "sql" [:small " Remixed from " (link-to-mybatis)]]]]))