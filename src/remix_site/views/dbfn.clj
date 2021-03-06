(ns remix-site.views.dbfn
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]]
        [remix-site.views.common :only [layout link-to-korma link-to-jdbc link-to-remix layout-snippets clj-snippet]]))

(defrh "/dbfn" []
  (layout
   "dbfn"
   [:div.page-header
    [:h1 "dbfn" [:small " Remixed from " (link-to-korma)]]]
   [:p (link-to-remix "dbfn.clj" "Machinery")
    " for database functions with " (link-to "/sql" "sql") " and " (link-to "/db" "db") "."]
   (layout-snippets "
The namespace for dbfn is remix.dbfn" "
=> (require '(remix [dbfn :as d]))"

"Define a select."
"=> (d/defselect fruit db (d/sql \"select * from fruit\"))"

"And call it."
"=> (fruit)
[]"

"Browse the resultset."
"=> (require '(remix [browse :only [browse-resultset]]))
=> (browse-result (fruit))"

"Define an insert and call it."
"=> (d/definsert insert-fruit db
      (d/sql \"insert into fruit (id, name, appearance)
              values (:id, :name, :appearance)\"))
=> (insert-fruit :id 11 :name \"apple\" :appearance \"red\")
=> (fruit)
[{:id 11, :name \"apple\", :appearance \"red\", :cost nil, :grade nil}]"

"Update it."
"=> (d/defupdate update-fruit db
      (d/sql \"update fruit set name = :name, appearance = :appearance
              where id = :id\"))
=> (update-fruit :id 11 :name \"orange\" :appearance \"orange\")"

"Delete it"
"=> (d/defdelete delete-fruit db (d/sql \"delete from fruit where id = :id\"))
=> (delete-fruit :id 11)"

"Add a doc string and arg keys, a collection of keys to zip map against the args to form the param map."
"=> (d/defselect fruit-by-name-and-cost db
      (d/doc \"Returns fruit by name and cost.\")
      (d/argkeys [:name :cost])
      (d/sql \"select * from fruit where name = :name and cost = :cost\"))"

"Let's take a look at the sql."
"=> (d/sql-only (fruit-by-name-cost \"kiwi\" 1))
[\"select * from fruit where name = ? and cost = ?\" \"kiwi\" 1]"

"Which keywords are used?"
"=> (d/keywords-only (fruit-by-name-cost \"kiwi\" 1))
[\"select * from fruit where name = ? and cost = ?\" :name :cost]"

"Substitute the values corresponding to the keywords."
"(d/keywords-only! (fruit-by-name-cost \"kiwi\" 1))
[\"select * from fruit where name = 'kiwi' and cost = 1\"]"

"Prepare the parameters and transform the results."
"=> (d/defselect fruit-by-name db
      (d/argkeys [:name])
      (d/prepare (fn [m] (update-in m [:name] str \"-1\")))
      (d/sql \"select name, appearance from fruit where name = :name\")
      (d/transform (fn [rs] (update-in rs [0 :name] str \"-2\"))))
=> (fruit-by-name \"watermelon\")
[{:name \"watermelon-1-2\" :appearance \"pink\"}]"

(html "Get the generated key from an insert. Uses param-keys and param-vals from " (link-to "/sql" "sql")  ".")
"=> (d/definsert insert-generated-key db
      (d/argkeys [:id :name :appearance])
      (d/generated-keys [:ID]) ; hsqldb cap rules.
      (d/sql \"insert into fruit (\" remix.sql/params-keys \")
              values (\" remix.sql/param-vals \")\"))
=> (insert-generated-key 1 \"apple\" \"red\")
[{:id 1}]"

"Insert multiple."
"=> (d/definsert insert-multiple db
      (d/sql \"insert into fruit (\" remix.sql/param-keys \")
              values (\" remix.sql/param-vals \")\"))
=> (insert-multiple [{:id 1 :name \"apple\" :appearance \"red\"}
                     {:id 2 :name \"orange\" :appearance \"orange\"}])
(1 1)")))