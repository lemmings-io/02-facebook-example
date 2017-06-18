(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [fb-messenger.send :as facebook]
            [facebook-example.reaction :as reaction]))

; Uncomment if you want to set a peristent menu in your bot:
; (facebook/set-messenger-profile
;      {:get_started {:payload "get-started"}
;       :persistent_menu [{:locale "default"
;                          :call_to_actions [{:title "Help"
;                                             :type "postback"
;                                             :payload "get-help"}]}]})

(defn on-message [event]
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

(defn on-postback [event]
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
  (println "on-attachment event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        attachments (get-in event [:message :attachments])]
    (reaction/thank-for-attachment)))

; You should not need to touch the following code :)
(defn postback? [messaging-event](contains? messaging-event :postback))
(defn attachments? [messaging-event] (contains? (:message messaging-event) :attachments))
(defn message? [messaging-event] (contains? messaging-event :message))

(defn handle-message [messaging-event]
  (let [sender-id (get-in messaging-event [:sender :id])
        replies (cond
                  (postback? messaging-event) (on-postback messaging-event)
                  (message? messaging-event)
                  (cond
                    (attachments? messaging-event)  (on-attachments messaging-event)
                    :else                           (on-message messaging-event))
                  :else (println (str "Webhook received unknown messaging-event: " messaging-event)))]
    (doseq [message replies]
      (facebook/send-message sender-id message))))
