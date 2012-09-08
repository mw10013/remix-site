(ns remix-site.views.rhandler
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-ring link-to-compojure link-to-noir]]))

(declare wrap-rhandler-snippet)

(defrh "/rhandler" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "rhandler " [:small "Remixed from " (link-to-noir)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/rhandler.clj" "Machinery ") "to define/re-define
route handlers on top of " (link-to-compojure)  "."]
    [:div.row
     [:div.span6
      [:p "Use " [:code "(wrap-rhandler handler & ns-syms)"] " middleware to hook rhandler's into " (link-to-ring) ".
ns-syms should refer to namespaces and they and their children are required."]]
     [:div.span6 (wrap-rhandler-snippet)]]
    [:p "Define a route handler with " [:code "(defrh name? method? path bindings & body)"] "."]
    [:ul
     [:li "When specified, defn's function called " [:code "name"] " taking a ring request so you can call
the route handler programatically."]
     [:li [:code "method"] " can be any of Composure's route macro names  as lower-case keywords. For instance, Compojure's "
      [:code "POST"] " becomes " [:code ":post"] ". The default is " [:code ":get"] "."]
     [:li [:code "path"] " is a Composure path to "
      (link-to "https://github.com/weavejester/compojure/wiki/Routes-In-Detail" "match against the URI") "."]
     [:li [:code "bindings"] " follow Composure's "
      (link-to "https://github.com/weavejester/compojure/wiki/Destructuring-Syntax" "destructuring syntax") "."]
     [:li [:code "body"] " should return the handler's response."]]]))

(defhtml wrap-rhandler-snippet []
  [:pre.prettyprint.lang-clj"
(ns remix-site.views.rhandler
  (:use [remix.rhandler :only [wrap-rhandler]]))

(def app (-> routes (wrap-rhandler 'remix-site.views) site))"])