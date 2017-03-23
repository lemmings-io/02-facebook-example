(ns facebook-example.bot
  (:gen-class)
  (:require [clojure.string :as s]
            [environ.core :refer [env]]
            [fb-messenger.send :as fb]
            [fb-messenger.templates :as tmpl]))

(defn on-message [payload]
  (println "on-message payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        message-text (get-in payload [:message :text])]
    (cond)))
      (s/includes? (s/lower-case message-text) "help") (fb/send-message sender-id (tmpl/text-message "Hi there, happy to help :)"))
      (s/includes? (s/lower-case message-text) "image") (fb/send-message sender-id (tmpl/image-message "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/M101_hires_STScI-PRC2006-10a.jpg/1280px-M101_hires_STScI-PRC2006-10a.jpg"))
      ; If no rules apply echo the user's message-text input
      :else (fb/send-message sender-id (tmpl/text-message message-text))

(defn on-postback [payload]
  (println "on-postback payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        postback (get-in payload [:postback :payload])
        referral (get-in payload [:postback :referral :ref])]
    (cond
      (= postback "GET_STARTED") (fb/send-message sender-id (tmpl/text-message "Welcome =)"))
      :else (fb/send-message sender-id (tmpl/text-message "Sorry, I don't know how to handle that postback")))))

(defn on-attachments [payload]
  (println "on-attachment payload:")
  (println payload)
  (let [sender-id (get-in payload [:sender :id])
        recipient-id (get-in payload [:recipient :id])
        time-of-message (get-in payload [:timestamp])
        attachments (get-in payload [:message :attachments])]
    (fb/send-message sender-id (tmpl/text-message "Thanks for your attachments :)"))))

(defn postback? [messaging-event] (contains? messaging-event :postback))
(defn attachments? [messaging-event] (contains? (:message messaging-event) :attachments))
(defn message? [messaging-event] (contains? messaging-event :message))

(defn handle-message [payload]
  (condp
    (postback? messaging-event)
    (on-postback messaging-event)

    (message? messaging-event
      (cond
        (attachments? messaging-event)
        (on-attachments messaging-event)

        :else
        (on-message messaging-event)))

    :else
    (println (str "Webhook received unknown messaging-event: " messaging-event))))
