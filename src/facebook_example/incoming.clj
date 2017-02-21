(ns facebook-example.incoming
  (:gen-class)
  (:require [clojure.string :as string]
            [fb-messenger.send :as facebook]
            [facebook-example.outgoing :as outgoing]))


; As you can see below, Clojure already provides string/includes? to check if one string is included in another string
; (string/includes? "Hello Lemming" "Hello") => true
; We define our own function using string/includes? to check if message-text includes any one of a list of strings
(defn includes-any? [message-text list-of-strings]
  (some #(string/includes? message-text %) list-of-strings))

(defn init [])
  ; Uncomment if you want to set a persistent menu in your bot:
  ; (facebook/set-messenger-profile
  ;      {:get_started {:payload "GET_STARTED"}
  ;       :persistent_menu [{:locale "default"
  ;                          :call_to_actions [{:title "Help"
  ;                                             :type "postback"
  ;                                             :payload "GET_HELP"}
  ;                                            {:title "Show me some bots"
  ;                                             :type "postback"
  ;                                             :payload "GET_LEMMINGS_BOTS"}]}]}))

(defn on-message [event]
  ; Called by handle-message when the user has sent a text message
  (println "on-message event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        message-text (get-in event [:message :text])
        lower-case-message-text (string/lower-case message-text)
        user-data (facebook/get-user-profile sender-id)]
    (cond
      (includes-any? lower-case-message-text ["hi" "hello" "hallo" "hey" "hy"]) (outgoing/welcome (get-in user-data [:first_name]))
      (string/includes? lower-case-message-text "help") (outgoing/help)
      (string/includes? lower-case-message-text "image") (outgoing/some-image)
      (string/includes? lower-case-message-text "bots") (outgoing/send-lemmings-bots)
      ; If no rules apply echo the user's message-text input
      :else (outgoing/echo message-text))))

(defn on-quick-reply [event]
  ; Called by handle-message when the user has tapped a quick reply
  ; https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
  (println "on-quickreply event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        quick-reply (get-in event [:message :quick_reply])
        quick-reply-payload (get-in quick-reply [:payload])
        user-data (facebook/get-user-profile sender-id)]
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
        referral (get-in event [:postback :referral :ref])
        user-data (facebook/get-user-profile sender-id)]
    (cond
      (= postback "GET_STARTED") (outgoing/welcome)
      (= postback "GET_HELP") (outgoing/help)
      (= postback "GET_LEMMINGS_BOTS") (outgoing/send-lemmings-bots)
      :else (outgoing/error))))

(def compliments ["These are beautiful eyes!" "I like this nose!" "Wow, those lips!" "Nice ears!"])
(defn on-image [sender-id attachment]
  ;;; see vision.clj:22 and https://cloud.google.com/vision/docs/how-to
  ;;; for further information about the vision api
  (let [vision-response (vision/analyze (get-in attachment [:payload :url]))]
    (cond
      (contains? vision-response :faceAnnotations)
      (let [face-annotations (:faceAnnotations vision-response)]
        (fb/send-message sender-id (fb/text-message (rand-nth compliments))))
      (contains? vision-response :labelAnnotations)
      (let [label-annotations (:labelAnnotations vision-response)]
        (let [firstLabel (:description (first label-annotations))]
          (fb/send-message sender-id (fb/text-message (str "Is that your " firstLabel "? It's beautiful!")))))
      :else (fb/send-message sender-id (fb/text-message "Uhm, I'm not sure what that is, but its beautiful!")))))

(defn on-audio [sender-id attachment]
  (fb/send-message sender-id (fb/text-message "That sounds beautiful!")))

(defn on-attachments [event]
  ; Called by handle-message when the user has sent a file or sticker
  (println "on-attachment event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        attachments (get-in event [:message :attachments])
        user-data (facebook/get-user-profile sender-id)]
    (let [attachment (first attachments)]
      (cond
        (= (:type attachment) "image") (on-image sender-id attachment)
        (= (:type attachment) "audio") (on-audio sender-id attachment)
        :else (outgoing/thank-for-attachment)))))
