(ns remix-site.views.sql
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]]]
        [remix-site.views.common :only [layout layout-snippets link-to-mybatis link-to-jdbc link-to-remix clj-snippet]]))

(defhtml snippet [s1 s2]
  [:p s1]
  [:p (clj-snippet s2)])

(defrh "/sql" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "sql" [:small " Remixed from " (link-to-mybatis)]]]
    [:p (link-to-remix "sql.clj" "Machinery")
     " for dynamic sql."]
    (layout-snippets "
The namespace for dynamic SQL is remix.sql." "
=> (require '(remix [sql :as sql]))"

"Compile sql:"
"=> (def snippet (sql/sql \"select * from table where title = :title\"))
=> snippet
(\"select * from table where title = \" :title)"

"Prepare it with a parameter map:"
"=> (sql/prepare {:title \"the-title\"} sql)
[\"select * from table where title = ?\" \"the-title\"]"

"Conditionally include a part of a where clause:"
"=> (sql/prepare {:title \"the-title\"} (sql/sql \"select * from blog where state = 'ACTIVE'\"
     (sql/when :title \"and title like :title\")))
[\"select * from blog where state = 'ACTIVE'and title like ?\" \"the-title\"]"

"sql/when takes a predicate of one arg, a parameter map:"
"=> (sql/prepare {:title \"the-title\"} (sql/sql \"select * from blog where state = 'ACTIVE'\"
     (sql/when #(:title %) \"and title like :title\")))
[\"select * from blog where state = 'ACTIVE'and title like ?\" \"the-title\"]"

"sql/where trims the first and/or in the where clause:"
"=> (sql/prepare {:title \"the-title\"} (sql/sql (sql/where (sql/when :author \"and author = :author\")
                                                         (sql/when :title \"and title = :title\"))))
[\" where title = ?\" \"the-title\"]"

"sql/set trims the last comma:"
"=> (sql/prepare {:title \"the-title\" :author \"clinton\"}
                  (sql/sql (sql/set (sql/when :title \"title = :title,\")
                                    (sql/when :author \"author = :author,\"))))
[\" set title = ?, author = ?\" \"the-title\" \"clinton\"]"

"Using vars:"
"=> (def cols (sql/sql \"col1, col2, col3\"))
=> (def title (sql/sql \"and title = :title\"))
=> (def ^:const const-val 3)
=> (sql/prepare {:title \"the-title\"} (sql/sql \"select \" #'cols \" from table\"
                                                (sql/where title \"and col1 = \" const-val)))
[\"select col1, col2, col3  from table where title = ? and col1 =  3\" \"the-title\"]"

"sql/cond chooses the first non-empty sql string:"
"=> (sql/prepare {} (sql/sql (sql/cond (sql/when :title \"title = :title\")
                                      (sql/when :author \"author = :author\")
                                      \"otherwise\")))
[\"otherwise\"]"

"sql/coll builds a collection:"
"=> (sql/prepare {:coll [\"a\" \"b\" \"c\"]} (sql/sql (sql/coll :coll)))
[\"('a', 'b', 'c')\"]")]))