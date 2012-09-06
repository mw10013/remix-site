(ns remix-site.views.validate
  (:use [remix [rhandler :only [defrh]] [validate :only [invalid?]]]
        [hiccup [core :only [html h]] [page :only [html5 include-css include-js]]
         [element :only [link-to]]
         [form :only [form-to submit-button label text-field password-field]]]
        [remix-site.views.common :only [layout]]))

(defrh validate "/validate" {:keys [params flash errors] :as req}
  (println params)
  (layout
   [:div.container
    [:div.page-header
     [:h1 "Validate " [:small "With apologies to "
                       (link-to "https://github.com/weavejester/valip" "valip")]]]
    (when flash (html [:div.alert.alert-success.fade.in
                       [:button.close {:type :button :data-dismiss :alert} "x"]
                       flash]))
    (form-to {:class :form-horizontal} [:post "/validate-postback"]
             [:legend "Validate form example"]
             [(if (:name errors) :div.control-group.error :div.control-group)
              (label {:class :control-label :for :name} :name "Name")
              [:div.controls
               (text-field :name (:name params))
               (when (:name errors) [:span.help-inline (clojure.string/join \space (:name errors))])]]
             [:div.control-group
              (label {:class :control-label :for :pw} :pw "Password")
              [:div.controls
               (password-field :pw)]]
             [:div.control-group
              (label {:class :control-label :for :confirm} :confirm "Confirm Password")
              [:div.controls
               (password-field :confirm)]]
             [:div.form-actions
              (submit-button {:class "btn btn-primary"} "Submit")])]))

(defrh :post "/validate-postback" {:keys [params] :as req}
  (if-let [errors (invalid? params [:name (complement clojure.string/blank?) "Required."])]
    (validate (assoc req :errors errors))
    (validate (assoc req :errors nil :flash "Validated"))))
