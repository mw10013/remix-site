(ns remix-site.views.nested-params
  (:use [clojure.pprint :only [pprint]]
        [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html h]] [def :only [defhtml]] [form :only [form-to submit-button label text-field]]]
        [remix-site.views.common :only [layout link-to-ring link-to-remix clj-snippet]]))

(declare wrap-nested-params-snippet degenerate-case-snippet)

(defhtml field [path params]
  [:div.control-group
   (label {:class :control-label :for path} path (str \[ (apply str (interpose "&nbsp;" path)) \]))
   [:div.controls
    (text-field path (get-in params path))]])

(defhtml form [params]
  (form-to {:class :form-horizontal} [:post "/nested-params-postback"]
           (when (:as params)
             (html [:div.alert.alert-info.fade.in
                    [:button.close {:type :button :data-dismiss :alert} "x"]
                    (clj-snippet (-> params (select-keys [:as]) pprint with-out-str))]))
           (field [:as 0 :id] params)
           (field [:as 0 :bs 0 :id] params)
           (field [:as 0 :bs 1 :id] params)
           (field [:as 0 :bs 0 :cs 0 :id] params)
           (field [:as 0 :bs 0 :cs 1 :id] params)
           (field [:as 0 :bs 1 :cs 0 :id] params)
           (field [:as 2 :bs 2 :cs 2 :id] params)
           [:div.form-actions
            (submit-button {:class "btn btn-primary"} "Submit")]))

(defrh nested-params "/nested-params" {:keys [params] :as req}
  (layout
   "nested-params"
   [:div.page-header
    [:h1 "nested-params " [:small "Remixed from nested-params in " (link-to-ring)]]]
   [:p (link-to-remix "middleware/nested_params.clj" "Machinery")
    " to convert a single-depth map of parameters to a nested map as ring middleware."]
   [:div.row
    [:div.span6
     [:p "Use " [:code "(wrap-nested-params handler)"] " middleware as a drop-in replacement for nested-params in " (link-to-ring) "."]
     [:p "Extends the nested key syntax by accepting parameter names as vectors of keys. Keys may be keywords or integers,
which will be treated as indexes into nested vectors. If any levels does not exist, hash-maps and vectors will be created."]
     [:p "The labels in Kick the Tires below contain examples of this syntax."]]
    [:div.span6 (wrap-nested-params-snippet)]]
   [:div.row     
    [:div.span6 (degenerate-case-snippet)]
    [:div.span6
     [:p [:span.label.label-important "Important"] " Breaks compatibility with ring's nested-params in the degenerate case."]]]
   [:h2 "Kick the Tires"]
   (form params)))

(defrh :post "/nested-params-postback" {:keys [params] :as req}
  (nested-params (assoc req :flash params)))

(defn- wrap-nested-params-snippet []
  (clj-snippet "(ns remix-site.app
  (:use [remix.middleware.nested-params
         :only [wrap-nested-params]]))

  (def app (-> routes
               wrap-keyword-params
               wrap-nested-params
               wrap-params))"))

(defn- degenerate-case-snippet []
  (clj-snippet "(use 'ring.middleware.nested-params)
(def handler (wrap-nested-params :params))
(handler {:params {\"[:as 0 :id]\" \"bar\"}})
; {\"\" {\":as 0 :id\" \"bar\"}}"))