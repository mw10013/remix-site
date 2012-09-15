(ns remix-site.views.db
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-korma link-to-jdbc clj-snippet]]))

(declare create-db-snippet)

(defrh "/db" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "db" [:small " Remixed from " (link-to-korma)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/db.clj" "Machinery")
     " to manage database connections with " (link-to "http://www.mchange.com/projects/c3p0/" "c3p0") "."]
    [:div.row
     [:div.span5
      [:p "Use " [:code "(create-db db-spec)"] " to create a c3p0 connection pool."]
      [:p [:code "db-spec"] " is a map with the following keys:"]
      [:ul
       [:li [:code ":datasource-spec"] " for " (link-to-jdbc)
        [:ul
         [:li [:code ":subprotocol"] " jdbc protocol"]
         [:li [:code ":subname"] " jdbc subname "]
         [:li [:code ":classname"] " jdbc driver class name"]
         [:li [:code ":username"]]
         [:li [:code ":password"]]]]
       [:li [:code ":pool-spec"]
        [:ul
         [:li [:code ":max-idle-time-excess-in-sec"]]
         [:li [:code ":max-idle-time-in-sec"]]]]
       [:li [:code ":naming-strategy"] " for " (link-to-jdbc) " and is optional."]]]
     [:div.span7 (create-db-snippet)]]]))

(defn- create-db-snippet []
  (clj-snippet "
(use 'remix.db)
(create-db
 {:datasource-spec
  {:classname \"org.hsqldb.jdbcDriver\"
   :subprotocol \"hsqldb\"
   :subname \"remix_test_hsqldb\"}
  :pool-spec {:idle-time-excess-in-sec (* 15 60)
              :idle-time (* 30 60)}
  :naming-strategy {:keys #(-> % clojure.string/lower-case
                               (clojure.string/replace \\_ \\-))
                    :fields #(clojure.string/replace % \\- \\_)}}) "))

(comment
  (defn create-db [db-spec]
  (let [datasource-spec (:datasource-spec db-spec)
        pool-spec (:pool-spec db-spec)
        cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname datasource-spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol datasource-spec) ":" (:subname datasource-spec)))
               (.setUser (:user datasource-spec))
               (.setPassword (:password datasource-spec))
               (.setMaxIdleTimeExcessConnections (or (:max-idle-time-excess-in-sec pool-spec)
                                                     (* 15 60)))
               (.setMaxIdleTime (or (:max-idle-time-in-sec pool-spec) (* 30 60))))]
    {:connection-spec {:datasource cpds}
     :naming-strategy (:naming-strategy db-spec)})))