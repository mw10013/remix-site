(ns remix-site.views.bootstrap
  (:require [remix [bootstrap :as bs]])
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-remix clj-snippet]]))

(defrh "/bootstrap" req
  (layout
   "bootstrap"
   [:div.page-header
    [:h1 "bootstrap"]]
   [:p (link-to-remix "bootstrap.clj" "Machinery ") "for "
    (link-to "http://twitter.github.com/bootstrap/index.html" "twitter bootstrap.")]))