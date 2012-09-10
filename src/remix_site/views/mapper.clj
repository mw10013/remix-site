(ns remix-site.views.mapper
  (:use [clojure.pprint :only [pprint]]
        [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html h]] [def :only [defhtml]] [element :only [link-to]]
         [form :only [form-to submit-button label text-field]]]
        [remix-site.views.common :only [layout link-to-mybatis clj-snippet]]))

(defrh mapper "/mapper" []
  (layout
   [:div.container
    [:div.page-header
     [:h1 "mapper " [:small "Remixed from " (link-to-mybatis)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/mapper.clj" "Machinery")
     " to map values of maps and reduce result sets to nested maps"]
    [:div.row
     [:div.span6
      [:p "Use " [:code "(wrap-nested-params handler)"] " middleware as a drop-in replacement for nested-params in " (link-to-mybatis) "."]
      [:p "Extends the nested key syntax by accepting parameter names as vectors of keys. Keys may be keywords or integers,
which will be treated as indexes into nested vectors. If any levels do not exist, hash-maps and vectors will be created."]
      [:p "The labels in Kick the Tires below contain examples of this syntax."]]
     [:div.span6 #_(wrap-nested-params-snippet)]]
    [:div.row     
     [:div.span6 #_(degenerate-case-snippet)]
     [:div.span6
      [:p [:span.label.label-important "Important"] " Breaks compatibility with ring's nested-params in the degerate case."]]]]))

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
  (clj-snippet "("))