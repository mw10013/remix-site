(ns remix-site.views.validate
  (:use [remix [rhandler :only [defrh]] [validate :only [invalid?]]]
        [hiccup [core :only [html h]] [page :only [html5 include-css include-js]]
         [def :only [defhtml]] [element :only [link-to]]
         [form :only [form-to submit-button label text-field password-field]]]
        [remix-site.views.common :only [layout]]
        [clojure.string :only [join blank?]]))

(declare clj-snippet)

(defmacro control-group [k label errors & body]
  `[(if (~k ~errors) :div.control-group.error :div.control-group)
    (label {:class :control-label :for ~k} ~k ~label)
    [:div.controls
     ~@body
     (when (~k ~errors) [:span.help-inline (join \space (~k ~errors))])]])

(defrh validate "/validate" {:keys [params flash errors] :as req}
  (layout
   [:div.container
    [:div.page-header
     [:h1 "Validate " [:small "With apologies to "
                       (link-to "https://github.com/weavejester/valip" "valip")]]]
    (when flash (html [:div.alert.alert-success.fade.in
                       [:button.close {:type :button :data-dismiss :alert} "x"]
                       flash]))
    (clj-snippet)
    (form-to {:class :form-horizontal} [:post "/validate-postback"]
             [:legend "Validate form example"]
             (control-group :name "Name" errors (text-field :name (:name params)))
             (control-group :password "Password" errors (password-field :password (:password params)))
             (control-group :confirm-password "Confirm Password" errors (password-field :confirm-password (:confirm-password params)))
             (control-group :fav-num "Favorite Number" errors (text-field :fav-num (:fav-num params)))
             [:div.form-actions
              (submit-button {:class "btn btn-primary"} "Submit")])]))

(defrh :post "/validate-postback" {:keys [params] :as req}
  (if-let [errors (invalid? params
                            [:name (complement blank?) "Required."]
                            [[:password (complement blank?) "Required."]
                             [:confirm-password (complement blank?) "Required."]
                             [identity :password #(apply = (map % [:password :confirm-password])) "Passwords must match."]]
                            [:fav-num #(or (blank? %) (try (Double. %) (catch Exception e false))) "Invalid number."])]
    (validate (assoc req :errors errors))
    (validate (assoc req :errors nil :flash "Valid."))))

(defhtml clj-snippet []
  [:pre.prettyprint.lang-clj.linenums "
(ns remix-site.views.validate
  (:use [remix.validate :only [invalid?]]))"])