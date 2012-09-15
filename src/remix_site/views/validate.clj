(ns remix-site.views.validate
  (:use [remix [rhandler :only [defrh]] [validate :only [invalid?]]]
        [hiccup [core :only [html]] [def :only [defhtml]] [element :only [link-to]]
         [form :only [form-to submit-button label text-field password-field]]]
        [remix-site.views.common :only [layout link-to-valip link-to-remix clj-snippet]]
        [clojure.string :only [join blank?]]))

(declare validate-snippet)

(defmacro control-group [k label errors & body]
  `[(if (~k ~errors) :div.control-group.error :div.control-group)
    (label {:class :control-label :for ~k} ~k ~label)
    [:div.controls
     ~@body
     (when (~k ~errors) [:span.help-inline (join \space (~k ~errors))])]])

(defhtml form [params errors flash]
  (form-to {:class :form-horizontal} [:post "/validate-postback"]
           (when flash (html [:div.alert.alert-success.fade.in
                              [:button.close {:type :button :data-dismiss :alert} "x"]
                              flash]))
             (control-group :name "Name" errors (text-field :name (:name params)))
             (control-group :password "Password" errors (password-field :password (:password params)))
             (control-group :confirm-password "Confirm Password" errors (password-field :confirm-password (:confirm-password params)))
             (control-group :fav-num "Favorite Number" errors (text-field :fav-num (:fav-num params)))
             [:div.form-actions
              (submit-button {:class "btn btn-primary"} "Submit")]))

(defrh validate "/validate" {:keys [params flash errors] :as req}
  (layout
   [:div.container
    [:div.page-header
     [:h1 "validate " [:small "Remixed from " (link-to-valip)]]]
    [:p (link-to-remix "validate.clj" "Machinery ") "to validate a map."]
    [:p [:code "(invalid? map & rules)"] " validates map against rules returning a map of errors or nil."]
    [:p "Rules are vectors"
     [:ul
      [:li [:code "[key predicate error]"]]
      [:li [:code "[val-fn key predicate error]"]]]
     "The value for " [:code "key" ] " in the map to be validated is passed to " [:code "predicate"] ".
If " [:code "predicate"] " returns falsy, " [:code "error"] " is added to the error map " [:code "{key [error]}"] ". If "
     [:code "val-fn"] " is specified, it takes the map to be validated and the result is passed to " [:code "predicate"] "."]
    [:p "A rule can also be a collection containing rules and the first invalid rule gets its " [:code "error"] " into the error map."]
    [:h2 "Kick the Tires"]
    (form params errors flash)
    [:h2 (link-to "https://github.com/mw10013/remix-site/blob/master/src/remix_site/views/validate.clj" "Code Behind")]
    (validate-snippet)]))

(defrh :post "/validate-postback" {:keys [params] :as req}
  (if-let [errors (invalid? params
                            [:name (complement blank?) "Required."]
                            [[:password (complement blank?) "Required."]
                             [:confirm-password (complement blank?) "Required."]
                             [identity :password #(apply = (map % [:password :confirm-password])) "Passwords must match."]]
                            [:fav-num #(or (blank? %) (try (Double. %) (catch Exception _ false))) "Invalid number."])]
    (validate (assoc req :errors errors))
    (validate (assoc req :errors nil :flash "Valid."))))

(defn- validate-snippet []
  (clj-snippet "
(ns remix-site.views.validate
  (:use [remix.validate :only [invalid?]]))

(defrh :post \"/validate-postback\" {:keys [params] :as req}
  (if-let [errors (invalid? params
                            [:name (complement blank?) \"Required.\"]
                            [[:password (complement blank?) \"Required.\"]
                             [:confirm-password (complement blank?) \"Required.\"]
                             [identity :password
                              #(apply = (map % [:password :confirm-password]))
                              \"Passwords must match.\"]]
                            [:fav-num #(or (blank? %)
                                           (try (Double. %) (catch Exception _ false)))
                             \"Invalid number.\"])]
    (validate (assoc req :errors errors))
    (validate (assoc req :errors nil :flash \"Valid.\"))))"))