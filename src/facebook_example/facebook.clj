(ns facebook-example.facebook
  (:gen-class)
  (:require [clojure.string :as s]
            [facebook-example.messages :as msg]))

(defn webhook-is-valid? [request]
  (let [params (:params request)]
    (println "Incoming Webhook Request:")
    (println request)
    (if (= true (= (params "hub.mode") "subscribe")
          (= (params "hub.verify_token") (System/getenv "FB_PAGE_ACCESS_TOKEN")))
      {:status 200 :body (params "hub.challenge")}
      {:status 403})))

(defn processText [senderID text]
  (cond
    (s/includes? (s/lower-case text) "help") (msg/sendTextMessage [senderID "Hi there, happy to help :)"])
    (s/includes? (s/lower-case text) "image") (msg/sendImageMessage [senderID "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/M101_hires_STScI-PRC2006-10a.jpg/1280px-M101_hires_STScI-PRC2006-10a.jpg"])
    (s/includes? (s/lower-case text) "quick reply") (msg/sendQuickReply [senderID])
    ; If no rules apply echo the user's text input
    :else (msg/sendTextMessage [senderID text])))

; Process Received Message Event by Facebook
(defn receivedMessage [event]
  (println "Messaging Event:")
  (println event)
  (let [senderID (get-in event [:sender :id]) recipientID (get-in event [:recipient :id]) timeOfMessage (get-in event [:timestamp]) message (get-in event [:message])]
    (println (str "Received message for user " senderID " and page " recipientID " at " timeOfMessage " with message:"))
    (println message)
    ; TODO: Check for text (onText ?), attachments (onAttachments ?)
    ; or quick_reply (onQuickReply) in :message tree here
    ; TODO: Simplify function to send a message vs.
    ; (msg/sendTextMessage (receivedMessage messagingEvent))
    (let [messageText (message :text)]
      (cond
        ; Check for :text
        (contains? message :text) (processText senderID messageText)
        ; Check for :attachments
        (contains? message :attachments) (msg/sendTextMessage [senderID "Message with attachment received"])))))

(defn route-request [request]
  (let [data (get-in request [:params])]
    (println "Incoming Request:")
    (println request)
    (when (= (:object data) "page")
      (doseq [pageEntry (:entry data)]
        (doseq [messagingEvent (:messaging pageEntry)]
          ; Check for message (onMessage) or postback (onPostback) here
          (cond (contains? messagingEvent :message) (receivedMessage messagingEvent)
                (contains? messagingEvent :postback) (msg/sendTextMessage (receivedMessage messagingEvent))
                :else (println (str "Webhook received unknown messagingEvent: " messagingEvent))))))))
