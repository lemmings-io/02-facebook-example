(ns facebook-example.incoming
  (:gen-class)
  (:require [facebook-example.outgoing :as outgoing]))

; Uncomment if you want to set a persistent menu in your bot:
; (facebook/set-messenger-profile
;      {:get_started {:payload "GET_STARTED"}
;       :persistent_menu [{:locale "default"
;                          :call_to_actions [{:title "Help"
;                                             :type "postback"
;                                             :payload "GET_HELP"}]}]})
;                                             {:title "Show me some bots"}]))
;                                             :type "postback"
;                                             :payload "GET_LEMMINGS_BOTS"}

(defn on-message [event]
  ; Called by handle-message when the user has sent a text message
  (println "on-message event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        message-text (get-in event [:message :text])]

    (cond
      (re-matches #"(?i)hi|hello|hallo" message-text) (outgoing/welcome)
      (re-matches #"(?i)help" message-text) (outgoing/help)
      (re-matches #"(?i)image" message-text) (outgoing/some-image)
      (re-matches #"(?i)bots" message-text) (outgoing/send-lemmings-bots)
      ; If no rules apply echo the user's message-text input
      :else (outgoing/echo message-text))))

(defn on-quick-reply [event]
  ; Called by handle-message when the user has tapped a quick reply
  ; https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
  (println "on-quickreply event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        quick-reply (get-in event [:message :quick_reply])
        quick-reply-payload (:payload quick-reply)]
    (cond
      (= quick-reply-payload "CLOJURE") (outgoing/send-clojure-docs)
      (= quick-reply-payload "HEROKU") (outgoing/send-heroku-instructions)
      :else (outgoing/error))))

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
      (= postback "GET_STARTED") (outgoing/welcome)
      (= postback "GET_HELP") (outgoing/help)
      (= postback "GET_LEMMINGS_BOTS") (outgoing/send-lemmings-bots)
      :else (outgoing/error))))

(defn on-attachments [event]
  ; Called by handle-message when the user has sent a file or sticker
  (println "on-attachment event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        attachments (get-in event [:message :attachments])]
    (outgoing/thank-for-attachment)))
