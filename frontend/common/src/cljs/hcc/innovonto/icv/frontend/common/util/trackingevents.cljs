(ns hcc.innovonto.icv.frontend.common.util.trackingevents
  (:require [ajax.core :as ajax]))

(defn track [event urlconfig]
  {:method          :post
   :uri             (:tracking-endpoint urlconfig)
   :format          (ajax/json-request-format)
   :params          {:type (:type event) :payload (:payload event)}
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      [::tracking-event-success]
   :on-failure      [::tracking-event-error]})
