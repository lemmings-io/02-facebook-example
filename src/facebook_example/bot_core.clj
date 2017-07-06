(ns facebook-example.bot-core
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]
            [environ.core :refer [env]]
            [clojure.spec.alpha :as s]
            [clojure.future :refer :all]
            [fb-messenger.send :as facebook]
            [facebook-example.bot-custom :refer [on-quick-reply on-attachments
                                                 on-message on-postback]]))

; SCHEMA FOR REPLIES

(s/def ::action #{"typing_on" "typing_off" "mark_seen"})
(s/def ::duration int?)
(s/def ::delay int?)
(s/def ::message map?)

(s/def ::message-reply (s/keys :req-un [::message]
                               :opt-un [::delay]))

(s/def ::action-reply (s/keys :req-un [::action]
                              :opt-un [::duration]))

(s/def ::reply (s/or :message-reply ::message-reply
                     :action-reply ::action-reply))

; MATCH USER INPUT

(defn process-event [event]
  ; The order of the matching clauses is important!
  (match [event]
    ; The user has selected one quick-reply option
    [{:message {:quick_reply _}}]
    (on-quick-reply event)

    ; The user has sent a file or sticker
    [{:message {:attachments _}}]
    (on-attachments event)

    ; The user has sent a text message
    [{:message {:text _}}]
    (on-message event)

    ; The user has pressed a button for which a "postback" event has been defined
    [{:postback {:payload _}}]
    (on-postback event)

    :else
    (println (str "Webhook received unknown messaging-event: " event))))

(defn handle-message [messaging-event]
  (let [sender-id (get-in messaging-event [:sender :id])
        replies (process-event messaging-event)]
    (doseq [reply replies]

      ; If the reply does not conform to the spec, we throw with explanation
      (when-not (s/valid? ::reply reply)
        (throw (ex-info "The spec check has failed. Please make sure you are sending a correct reply pattern."
                        {:causes (s/explain-str ::reply reply)})))

      ; MATCH BOT REPLY
      ; The order of the matching clauses is important!
      (match [reply]

        ; The bot wants to send a message (text, images, videos etc.) after n milliseconds
        [{:message message :delay timeout}] 
        (do
          (Thread/sleep timeout)
          (facebook/send-message sender-id message))

        ; The bot wants to send a message (text, images, videos etc.)
        [{:message message}]
        (facebook/send-message sender-id message)
        
        ; The bot wants to perform an action for n milliseconds (typing_on in most cases)
        [{:action action :duration timeout}]
        (do
          (facebook/send-sender-action sender-id action)
          (Thread/sleep timeout))

        ; The bot wants to perform an action (mark_seen, typing_on, typing_off)
        [{:action action}]
        (facebook/send-sender-action sender-id action)

        :else
        (throw (ex-info "You have provided an invalid pattern in your reply."
                        {:causes reply}))))))
