(ns remix-site.views.nested-params
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-ring]]))

(defrh "/nested-params" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "nested-params " [:small "Remixed from nested-params in " (link-to-ring)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/middleware/nested_params.clj" "Machinery")
     " to convert a single-depth map of parameters to a nested map as ring middleware."]]))