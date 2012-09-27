(ns remix-site.views.rhandler
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-ring link-to-compojure link-to-noir link-to-remix clj-snippet]]))

(declare wrap-rhandler-snippet wrap-rhandler-slow-snippet defrh-snippet)

(defrh "/rhandler" []
  (layout
   [:div.page-header
    [:h1 "rhandler " [:small "Remixed from " (link-to-noir)]]]
   [:p (link-to-remix "rhandler.clj" "Machinery") " to define/re-define
route handlers on top of " (link-to-compojure)  "."]
   [:div.row
    [:div.span5
     [:p "Use " [:code "(wrap-rhandler handler load-handler & ns-prefixes)"] " middleware to hook into " (link-to-ring) ".
It will dispatch requests to handlers defined by " [:code "defrh"] ". If there is no matching defrh handler, "
      [:code "handler"] " is called."]]
    [:div.span7 (wrap-rhandler-snippet)]]
   [:div.row
    [:div.span9 (wrap-rhandler-slow-snippet)]
    [:div.span3
     [:p "Heroku and GAE insist your site be up relatively quickly. If your views take time to load, specify a "
      [:code "load-handler"] "."]
     [:p "The namespace " [:code "remix.slow.slowpoke"] " takes 45 seconds to load and may be useful in testing your "
      [:code "load-handler"] "."]]]
   [:h2 "defrh"]
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
    [:li [:code "body"] " should return the handler's response."]]
   [:span.label.label-info "Note"] " " (link-to-noir) " goes much further and is generally recommended.
If you decide not to use Noir, but want lightweight machinery to define/redefine route handlers,
then rhandler is available for remixing."
   [:h2 "Example"]
   (defrh-snippet)))

(defn- wrap-rhandler-snippet []
  (clj-snippet "
(ns remix-site.views.rhandler
  (:use [remix.rhandler :only [wrap-rhandler]]))

(def app (-> routes
             (wrap-rhandler nil \"remix-site.views\")
             site))"))

(defn- wrap-rhandler-slow-snippet []
  (clj-snippet "
(defroutes loading-routes
  (resources \"/\")
  (fn [req]
    (render
     (layout
      [:div.container
       [:div.alert.alert-info \"Site still loading. Please try again.\"]]) req)))

(def app (-> routes
             (wrap-rhandler loading-routes \"remix-site.views\" \"remix.slow\"))"))

(defn- defrh-snippet []
  (clj-snippet "
(ns remix-site.views.rhandler
  (:use [remix.rhandler :only [defrh]]))

; Named :get handler.
(defrh form-handler \"/form\" {:keys [errors]}
  (form-to [:post \"/form-postback\"]
           (when errors errors)
           (text-field :field)
           (submit-button \"Submit\")))

; Anonymous handler that calls form-handler.
(defrh :post \"/form-postback\" {:keys [params] :as request}
  (if-let [errors (invalid? params
                            [:field (complement blank?) \"Required.\"])]
    (form-handler (assoc request :errors errors))
    (form-handler (assoc request :errors nil))))"))
  