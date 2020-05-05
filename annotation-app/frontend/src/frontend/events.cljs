(ns frontend.events
  (:require
    [frontend.db :as db]
    [re-frame.core :refer [reg-event-db reg-event-fx]]))

(reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))


;; usage: (dispatch [:set-active-page {:page :home})
;; triggered when the user clicks on a link that redirects to a another page
(reg-event-fx
  ::set-active-page
  (fn [{:keys [db]} [_ {:keys [page mturk-metadata]}]]
    (let [set-page (assoc db :active-page page)]
      (println mturk-metadata)
      (case page
        ;;TODO get metadata, get preview or not

        :home {:db set-page}
        {:db set-page}))))
