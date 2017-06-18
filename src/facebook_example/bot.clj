(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [fb-messenger.send :as fb]
            [fb-messenger.templates :as response]
            [facebook-example.reaction :as reaction]
            [clojure.core.match :refer [match]]))

; Uncomment if you want to set a peristent menu in your bot:
; (facebook/set-messenger-profile
;      {:get_started {:payload "get-started"}
;       :persistent_menu [{:locale "default"
;                          :call_to_actions [{:title "Help"
;                                             :type "postback"
;                                             :payload "get-help"}]}]})

; Facebook sends us a rather complex JSON object with lots of nesting
; Before looking deeper into what we actually got, we pre-process this JSON
; and turn it into a simpler structure. This will make it much clearer
; to see the various states our bot can be in later.
(defn preprocess-event [event]
  (match [event]
    ; The user `sender-id` has sent us a message `text`.
    [{:message {:text text} :sender {:id sender-id}}]
    {:message (clojure.string/lower-case text) :text text :sender-id sender-id :event event}

    ; The user `sender-id` has pressed a button for which a "postback" event has been defined
    [{:postback {:payload postback} :sender {:id sender-id}}]
    {:postback postback :sender-id sender-id :event event}

    :else
    {:event event}))

; Here's where the logic of our bot lives.
(defn process-event [event]
  (match [event]
    [{:text (:or "hi" "hello" "lol")}]
    [(response/text-message "user said hi")
     (response/text-message "lol")]

    :else
    (println (str "unknown event: " event))))

(defn handle-event [raw-event]
  (let
    [event (preprocess-event raw-event)
     result (process-event event)]
    (doseq [message result] (fb/send-message (get event :sender-id) message))))
