(ns remix-site.views.mapper
  (:use [clojure.pprint :only [pprint]]
        [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html h]] [def :only [defhtml]] [element :only [link-to]]
         [form :only [form-to submit-button label text-field]]]
        [remix-site.views.common :only [layout link-to-mybatis clj-snippet]]))

(declare make-mapping-snippet apply-mappings-snippet make-mappings-snippet reduce-rows-snippet)

(defrh mapper "/mapper" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "mapper" [:small " Remixed from " (link-to-mybatis)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/mapper.clj" "Machinery")
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
        [:li [:code ":mappings"] " mappings to apply against values of ks for result."]
        [:li [:code ":children"] " vector of template maps."]]]]
     [:div.span8
      [:p (reduce-rows-snippet)]]]]))

(comment
  [clj-time [local :as ltime] [coerce :as ctime]]

  (let [mappings [(mpr/make-mappings [:created :min-date :max-date :factor-date :tenten-distrib-date]
                                   (comp ctime/to-timestamp ltime/to-local-date-time)
                                   ltime/to-local-date-time)
                [#(if-let [bond-type (:bond-type %)] (assoc % :bond-type-id (-> bond-type bond-types :id)) %)
                 #(if-let [bond-type-id (:bond-type-id %)] (assoc % :bond-type (id->bond-type bond-type-id)) %)]
                [#(if-let [sector (:sector %)] (assoc % :sector-id (-> sector sectors :id)) %)
                 #(if-let [sector-id (:sector-id %)] (assoc % :sector (id->sector sector-id)) %)]]]
  (def to-db ^{:doc "Map to database types."} (partial mpr/apply-mappings (map first mappings)))
  (def from-db ^{:doc "Map from database types."} (partial mpr/apply-mappings (map second mappings))))

(def to-formatted
  ^{:doc "Map from database types to formatted for display."}
  (partial from-db [#(if-let [bond-type (:bond-type %)] (assoc % :bond-type-name (get-in bond-types [bond-type :name])) %)
                    #(if-let [sector (:sector %)] (assoc % :sector-name (get-in sectors [sector :name])) %)
                    (fn [{:keys [eff-perf-100-price bond-group-type-id] :as m}]
                      (if eff-perf-100-price
                        (assoc m :eff-perf-100-price (if (= (id->bond-group bond-group-type-id) :generic)
                                                       (format "%,.0f" eff-perf-100-price)
                                                       (ticks/format-ticks eff-perf-100-price)))
                        m))
                    (fn [{:keys [weighted-porc-px bond-group-type-id] :as m}]
                      (if weighted-porc-px
                        (assoc m :weighted-porc-px (if (= (id->bond-group bond-group-type-id) :generic)
                                                       (format "%,.0f" weighted-porc-px)
                                                       (ticks/format-ticks weighted-porc-px)))
                        m))
                    (mpr/make-mapping #(ltime/format-local-time % :mmddyy-slash) :created)
                    (mpr/make-mapping ticks/format-ticks :offer-price :bid-price :mta-perf-porc-price
                                      :perf-porc-price :sub-io-perf-100-price :eff-perf-porc-price)
                    (mpr/make-mapping #(format "%,.0f" %) :loan-size :eff-oas)
                    (mpr/make-mapping #(format "%.1f" %) :offer-size :cheapness
                                      :eff-pp-err-cpr-03m :eff-pp-err-cpr-06m :eff-k1, :eff-k2, :eff-k3,
                                      :k1-yield :k2-yield :k3-yield :dq-yt1 :dq-yt2 :dq-yt3)
                    (mpr/make-mapping #(format "%.2f" %) :cap :coupon :cur-bal :current-credit-support :factor :floor :wac)
                    (mpr/make-mapping #(= % 1M) :traded-flag)]))

  )

(defn- make-mapping-snippet []
  (clj-snippet "(require '(remix [mapper :as m]))
(require '(clj-time local coerce))
(def mapping (m/make-mapping
              (comp clj-time.coerce/to-timestamp clj-time.local/to-local-date-time)
              :created :modified))
(mapping {:created (java.util.Date.) :modified nil})
; {:created #<Timestamp 2012-09-10 23:40:59.16>, :modified nil}"))

(defn- apply-mappings-snippet []
  (clj-snippet "(m/apply-mappings [mapping] {:created (java.util.Date.) :modified nil})
; {:created #<Timestamp 2012-09-10 23:30:08.727>, :modified nil}"))


(defn- reduce-rows-snippet []
  (clj-snippet "
(m/reduce-rows
 {:row-key :as :match-val-fn :a :ks [:a]
  :children [{:row-key :bs :match-val-fn :b :ks [:b]
              :children [{:row-key :cs :match-val-fn :c :ks [:c]
                          :mappings [(m/make-mapping inc :c)]}]}]}
 [{:a 1 :b 2 :c 3} {:a 2 :b 2 :c 3}])
; {:as [{:a 1 :bs [{:b 2 :cs [{:c 4}]}]} {:a 2 :bs [{:b 2 :cs [{:c 4}]}]}]}
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

