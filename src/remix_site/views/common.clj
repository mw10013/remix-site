(ns remix-site.views.common
  (:use [remix.rhandler :only [defrh]]
        [hiccup [core :only [html h]] [page :only [html5 include-css include-js]]
         [def :only [defhtml]] [element :only [link-to]]]))

(defn link-to-ring [] (link-to "https://github.com/ring-clojure/ring" "Ring"))
(defn link-to-compojure [] (link-to "https://github.com/weavejester/compojure" "Compojure"))
(defn link-to-jdbc [] (link-to "https://github.com/clojure/java.jdbc" "java.jdbc"))
(defn link-to-noir [] (link-to "https://github.com/noir-clojure/noir" "Noir"))
(defn link-to-korma [] (link-to "https://github.com/korma/Korma" "Korma"))
(defn link-to-mybatis [] (link-to "http://www.mybatis.org/core/" "MyBatis"))
(defn link-to-valip [] (link-to "https://github.com/weavejester/valip" "Valip"))
(defn link-to-remix [src content] (link-to (str "https://github.com/mw10013/remix/blob/master/src/remix/" src) content))

(defn layout [active & content]
  (html5 {:lang :en}
         [:head
          [:meta {:charset :utf-8}]
          [:title "Remix"]
          [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
          (include-css "/css/bootstrap.css")
          [:style {:type "text/css"} "
body {
  padding-top: 60px;
  padding-bottom: 40px;
}
"]
          (include-css "/css/bootstrap-responsive.css" "/css/prettify.css")"
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
  <script src='http://html5shim.googlecode.com/svn/trunk/html5.js'></script>
<![endif]-->

<script type=\"text/javascript\">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-35063750-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
"]
   [:body {:onload "prettyPrint()"}
    [:div.navbar.navbar-inverse.navbar-fixed-top
     [:div.navbar-inner
      [:div.container
       [:a.btn.btn-navbar {:data-toggle :collapse :data-target :.nav-collapse}
        (repeat 3 [:span.icon-bar])]
       (link-to {:class :brand} "/" "Remix")
       [:div.nav-collapse.collapse
        [:ul.nav
         (for [x ["rhandler" "validate" "nested-params" "mapper" "sql" "db" "dbfn" "bootstrap"]]
           [(if (= x active) :li.active :li) (link-to (str "/" x) x)])]]]]]
    [:div.container
     content
     [:hr]
     [:footer [:p "© Michael Wu 2012"]]]
    (include-js "/js/jquery.js" "/js/bootstrap.js" "/js/prettify.js" "/js/lang-clj.js")]))

(defhtml clj-snippet [s] [:pre.prettyprint.lang-clj.linenums (h s)])

(defn layout-snippets [& args]
  (for [[p s] (partition 2 args)]
    (html [:p p] [:p (clj-snippet s)])))

(defrh "/" []
  (layout
   nil
   [:div.hero-unit
    [:h1 "Remix"]
    [:p "Mix and match machinery in " (link-to "http://clojure.org/" "Clojure") " for web and sql."]
    [:p (link-to {:class "btn btn-primary btn-large"} "/rhandler" "Learn more &raquo;")]]
   [:div.row
    [:div.span4
     [:h2 "Machinery"]
     [:p "Take what you need. Discard the rest. Remix is not a framework. It is machinery that composes well with "
      (link-to-ring) ", " (link-to-compojure) ", and " (link-to-jdbc) "."]]
    [:div.span4
     [:h2 "Prior Art"]
     [:p "Remixed from the best including " (link-to-noir) ", " (link-to-korma) ", and " (link-to-mybatis) "."]]
    [:div.span4
     [:h2 "Context"]
     [:p (link-to "http://vimeo.com/14912890" "Everything is a remix.")]]]
   [:div.row
    [:div.span12 [:p [:span.label.label-info "Tip"] " If you are a beginner, consider " (link-to-noir) " and "
                  (link-to-korma) ". Both are wonderful and you may never look back. Later, if you decide you want to work
closer to the metal, then remix is avilable for remixing."]]]
   [:div.row
    [:p {:align :center} (link-to "https://clojars.org/org.clojars.mw10013/remix" "clojars") " |  "
     (link-to "https://github.com/mw10013/remix" "github")]]))

