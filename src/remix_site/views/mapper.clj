(ns remix-site.views.mapper
  (:use [clojure.pprint :only [pprint]]
        [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html h]] [def :only [defhtml]] [form :only [form-to submit-button label text-field]]]
        [remix-site.views.common :only [layout link-to-mybatis link-to-remix clj-snippet]]))

(declare make-mapping-snippet apply-mappings-snippet make-mappings-snippet reduce-rows-snippet)

(defrh mapper "/mapper" []
  (layout
   "mapper"
   [:div.page-header
    [:h1 "mapper" [:small " Remixed from " (link-to-mybatis)]]]
   [:p (link-to-remix "mapper.clj" "Machinery")
    " to map values of maps and reduce result sets to nested maps"]
   [:h2 "Mappings"]
   [:div.row
    [:div.span3
     [:p [:code "(make-mapping f k & ks)"] " returns a function taking a map. For each k, if the corresponding value
is not nil, passes it to f and updates the map with the result."]]
    [:div.span9 (make-mapping-snippet)]]
   [:div.row
    [:div.span8 (apply-mappings-snippet)]
    [:div.span4
     [:p [:code "(apply-mappings mapping-colls+ x)"] " takes one or more collections of mappings and
applies them against x, which is either a map or collection of maps."]]]
   [:div.row
    [:div.span12
     [:p [:code "(make-mappings ks & fns)"] " takes a collection of keys and functions. For every function,
it makes a mapping for the collection of keys. This is a way to pair up to/from conversion functions.
Partial up into functions to compose mappings."]
     [:p (make-mappings-snippet)]
     [:h2 "reduce-rows"]
     [:p "Use " [:code "(reduce-rows template rows)"] " to transform a collection of maps
into nested maps using a template. Can help with the N+1 selects database problem."]]]
   [:div.row
    [:div.span4
     [:p [:code "template"] " is a nested map with the following keys:"
      [:ul
       [:li [:code ":row-key"] " key to use in result map."]
       [:li [:code ":match-val-fn"] " function to match against a row."]
       [:li [:code ":ks"] " collection of keys to select into the result for matching rows."]
       [:li [:code ":transform-fn"] " fn transforming a map of ks for result."]
       [:li [:code ":children"] " vector of template maps."]]]]
    [:div.span8
     [:p (reduce-rows-snippet)]]]))

(defn- make-mapping-snippet []
  (clj-snippet "(require '(remix [mapper :as m]))
(require '(clj-time local coerce))
(def mapping (m/make-mapping
              (comp clj-time.coerce/to-timestamp
                    clj-time.local/to-local-date-time)
              :created :modified))
(mapping {:created (java.util.Date.) :modified nil})
; {:created #<Timestamp 2012-09-10 23:40:59.16>, :modified nil}"))

(defn- apply-mappings-snippet []
  (clj-snippet "
=> (m/apply-mappings [mapping] {:created (java.util.Date.)
                                :modified nil})
{:created #<Timestamp 2012-09-10 23:30:08.727>, :modified nil}"))

(defn- reduce-rows-snippet []
  (clj-snippet "
=> (m/reduce-rows
    {:row-key :as :match-val-fn :a :ks [:a]
     :children [{:row-key :bs :match-val-fn :b :ks [:b]
                 :children [{:row-key :cs :match-val-fn :c :ks [:c]
                             :transform-fn (partial m/apply-mappings
                                                    [(m/make-mapping inc :c)])}]}]}
     [{:a 1 :b 2 :c 3} {:a 2 :b 2 :c 3}])
{:as [{:a 1 :bs [{:b 2 :cs [{:c 4}]}]}
      {:a 2 :bs [{:b 2 :cs [{:c 4}]}]}]}
"))

(defn- make-mappings-snippet []
  (clj-snippet "
(let [mappings [(m/make-mappings [:created :modified]
                                 (comp clj-time.coerce/to-timestamp clj-time.local/to-local-date-time)
                                 (clj-time.local/to-local-date-time))]]
      (def to-db ^{:doc \"Map to database types.\"} (partial m/apply-mappings (map first mappings)))
      (def from-db ^{:doc \"Map from database types.\"}(partial m/apply-mappings (map second mappings)))
      (def to-formatted ^{:doc \"Map from database types to formatted for display.\"}
        (partial from-db
                 [(m/make-mapping #(clj-time.local/format-local-time % :basic-date) :created :modified)])))"))

