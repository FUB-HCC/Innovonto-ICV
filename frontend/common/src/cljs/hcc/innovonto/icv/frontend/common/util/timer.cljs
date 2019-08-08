(ns hcc.innovonto.icv.frontend.common.util.timer
  (:require
    [reagent.core :as reagent]
    [reagent.format :as fmt]
    [antizer.reagent :as ant]))

;;TODO subs

;;TODO events
;;TODO on-timer dispatch
;;TODO on-finish dispatch
;;Views
(defn- seconds-to-time [secs]
  (let [d (js/Date. (* secs 1000))]
    {:minutes (.getUTCMinutes d)
     :seconds (.getUTCSeconds d)}))

(defn format-time [seconds]
  (let [time-map (seconds-to-time seconds)]
    (reagent/as-element
     [:span
      [:span {:style {:font-size 12}} "Time left:"]
      [:br]
      (str (fmt/format "%02d" (:minutes time-map)) ":" (fmt/format "%02d" (:seconds time-map)))])))

(defn to-percent [timer]
  (* 100 (/ (:seconds timer) (:total-seconds timer))))


;;TODO set startTime
;;TODO setInterval
(defn timer-component [timer-sub]
  [ant/progress
   {:class-name     "timer"
    :percent        (to-percent @timer-sub)
    :stroke-linecap "square"
    :width          105
    :type           "dashboard"
    :status         (if (< 30 (to-percent @timer-sub)) "active" "exception")
    :format         #(format-time (:seconds @timer-sub))}])
