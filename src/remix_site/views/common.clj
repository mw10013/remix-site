(ns remix-site.views.common
  (:use [remix.rhandler :only [defrh]]
        [hiccup [page :only [html5 include-css include-js]]
         [element :only [link-to]]]))

(defn layout [content]
  (html5 {:lang :en}
   [:head
    [:meta {:charset :utf-8}]
    [:title "Heroku Sandbox"]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    (include-css "/css/bootstrap.css")
    [:style {:type "text/css"} "
body {
  padding-top: 60px;
  padding-bottom: 40px;
}
"]
    (include-css "/css/bootstrap-responsive.css")"
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
  <script src='http://html5shim.googlecode.com/svn/trunk/html5.js'></script>
<![endif]-->"]
   [:body
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.navbar-inner
      [:div.container
       [:a.btn.btn-navbar {:data-toggle :collapse :data-target :.nav-collapse}
        (repeat 3 [:span.icon-bar])]
       (link-to {:class :brand} "#" "Remix")
       [:div.nav-collapse.collapse
        [:ul.nav
         [:li.active (link-to "#home" "Home")]
         [:li (link-to "#about" "About")]]]]]]
    content]
   (include-js "js/jquery.js" "js/bootstrap.js")))

(defrh "/" []
  (layout
   [:div.container
    [:div.hero-unit
     [:h1 "Remix"]
     [:p "Mix and match machinery for web and sql."]
     [:p (link-to {:class "btn btn-primary btn-large"} "#" "Learn more &raquo;")]]]))


(defrh "/page" [] (layout nil))