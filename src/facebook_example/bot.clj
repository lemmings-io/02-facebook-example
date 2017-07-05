(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.core.match :refer [match]]
            [environ.core :refer [env]]
            [fb-messenger.send :as facebook]
            [facebook-example.reaction :as reaction]))

; Uncomment if you want to set a persistent menu in your bot:
; (facebook/set-messenger-profile
;      {:get_started {:payload "GET_STARTED"}
;       :persistent_menu [{:locale "default"
;                          :call_to_actions [{:title "Help"
;                                             :type "postback"
;                                             :payload "GET_HELP"}]}]})

(defn on-message [event]
  ; Called by handle-message when the user has sent a text message
  (println "on-message event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        message-text (get-in event [:message :text])]

    (cond
      (re-matches #"(?i)hi|hello|hallo" message-text) (reaction/welcome)
      (re-matches #"(?i)help" message-text) (reaction/help)
      (re-matches #"(?i)image" message-text) (reaction/some-image)
      ; If no rules apply echo the user's message-text input
      :else (reaction/echo message-text))))

(defn on-quick-reply [event]
  ; Called by handle-message when the user has tapped a quick reply
  ; https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
  (println "on-quickreply event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        quick-reply (get-in event [:message :quick_reply])
        quick-reply-payload (:payload quick-reply)]
    (cond
      (= quick-reply-payload "CLOJURE") (reaction/send-clojure-docs)
      (= quick-reply-payload "HEROKU") (reaction/send-heroku-instructions)
      :else (reaction/error))))

(defn on-postback [event]
  ; Called by handle-message when the user has tapped a postback button
  ; https://developers.facebook.com/docs/messenger-platform/send-api-reference/postback-button
  (println "on-postback event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        postback (get-in event [:postback :payload])
        referral (get-in event [:postback :referral :ref])]
    (cond
      (= postback "get-started") (reaction/welcome)
      (= postback "get-help") (reaction/help)
      :else (reaction/error))))

(defn on-attachments [event]
  ; Called by handle-message when the user has sent a file or sticker
  (println "on-attachment event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        attachments (get-in event [:message :attachments])]
    (reaction/thank-for-attachment)))

; You should not need to touch the following code :)
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
      (match [reply]
        ; The bot wants to send a message (text, images, videos etc.)
        [{:message message}] (facebook/send-message sender-id message)
        
        ; The bot wants to perform an action
        [{:action action}] (facebook/send-sender-action action)

        ; The bot wants to wait n milliseconds
        :else
        (println (str "Do clojure spec"))))))
