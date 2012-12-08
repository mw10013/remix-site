(ns remix-site.views.bootstrap
  (:require [remix [bootstrap :as bs]])
  (:use [remix [rhandler :only [defrh]]]
        [hiccup [element :only [link-to]] [form :only [form-to submit-button label text-field]]]
        [remix-site.views.common :only [layout link-to-remix clj-snippet]]))

(declare controls-snippet)

(defrh "/bootstrap" req
  (layout
   "bootstrap"
   [:div.page-header
    [:h1 "bootstrap"]]
   [:p (link-to-remix "bootstrap.clj" "Machinery ") "for "
    (link-to "http://twitter.github.com/bootstrap/index.html" "twitter bootstrap.")]
   [:h2 "Controls"]
   [:p [:code "(control-group k label errors & body"] " returns hiccup for a control group with a control-label label. errors is a map. If k has an entry in errors, the corresponding errors are displayed as help-inline."]
   [:p [:code "(control-group* label control help errors)"] " returns hiccup for a control group taking hiccup for label, control, and help along with a collection of errors."]
   [:p [:code "(control-actions & body"] " returns hiccup for a control actions."]
   [:p (form-to {:class :form-horizontal} [:get "/bootstrap"]
                      (bs/control-group :control "Control" nil (text-field :name))
                      (bs/control-group :control-error "Control (error)" {:control-error ["Control has error."]}
                                        (text-field :control-error))
                      (bs/control-group* (label {:class :control-label} :control-star "Control*")
                                         (text-field :control-star)
                                         [:span.help-block "A block of help text."] ["First error." "Second error."])
                      (bs/control-actions (submit-button {:class "btn btn-primary"} "Submit")))]
   (controls-snippet)
   [:h2 "Alerts"]
   [:p [:code "(alert attr-map? & content)"] " returns hiccup for an alert."]
   [:div.row
    [:div.span4
     [:p [:code "(alert \"Let this be a warning.\")"]]]
    [:div.span8 (bs/alert "Let this be a warning.")]]
   [:div.row
    [:div.span6 (bs/alert {:class :alert-error} "A dreadful error occurred.")]
    [:div.span6 [:p [:code "(alert {:class :alert-error} \"A dreadful error occurred.\")"]]]]))

(defn- controls-snippet []
  (clj-snippet "
(form-to {:class :form-horizontal} [:get \"/bootstrap\"]
              (bs/control-group :control \"Control\" nil (text-field :name))
              (bs/control-group :control-error \"Control (error)\" {:control-error [\"Control has error.\"]}
                                (text-field :control-error))
              (bs/control-group* (label {:class :control-label} :control-star \"Control*\")
                                 (text-field :control-star)
                                 [:span.help-block \"A block of help text.\"] [\"First error.\" \"Second error.\"])
              (bs/control-actions (submit-button {:class \"btn btn-primary\"} \"Submit\")))
"))