(ns remix-site.views.nested-params
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [core :only [html h]] [def :only [defhtml]] [element :only [link-to]]
         [form :only [form-to submit-button label text-field]]]
        [remix-site.views.common :only [layout link-to-ring]]))

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
                    [:pre (-> params (select-keys [:as]) clojure.pprint/pprint with-out-str h)]]))
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
   [:div.container
    [:div.page-header
     [:h1 "nested-params " [:small "Remixed from nested-params in " (link-to-ring)]]]
    [:p (link-to "https://github.com/mw10013/remix/blob/master/src/remix/middleware/nested_params.clj" "Machinery")
     " to convert a single-depth map of parameters to a nested map as ring middleware."]
    (form params)]))

(defrh :post "/nested-params-postback" {:keys [params] :as req}
  (nested-params (assoc req :flash params)))