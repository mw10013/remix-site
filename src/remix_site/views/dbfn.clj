(ns remix-site.views.dbfn
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-korma link-to-jdbc clj-snippet]]))

(defrh "/dbfn" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "dbfn" [:small " Remixed from " (link-to-korma)]]]]))