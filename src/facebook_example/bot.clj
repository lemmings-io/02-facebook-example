(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [fb-messenger.send :as facebook]
            [facebook-example.reaction :as reaction]
            [clojure.core.match :refer [match]]))

; Uncomment if you want to set a peristent menu in your bot:
; (facebook/set-messenger-profile
;      {:get_started {:payload "get-started"}
;       :persistent_menu [{:locale "default"
;                          :call_to_actions [{:title "Help"
;                                             :type "postback"
;                                             :payload "get-help"}]}]})


(defn handle-event [event]
  (match [event]
    :else []))
