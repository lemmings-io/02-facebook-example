(ns facebook-example.messages
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn sendAPI [messageData]
    (try
        (let [response (http/post "https://graph.facebook.com/v2.6/me/messages"
                        {:query-params {"access_token" (System/getenv "FB_PAGE_ACCESS_TOKEN")}
                         :headers {"Content-Type" "application/json"}
                         :body (json/write-str messageData)
                         :insecure? true})]
          (println "Response to FB:")
          (println @response))
        (catch Exception e (str "caught exception: " (.getMessage e)))))

(defn sendTextMessage [[recipientId messageText]]
    (let [messageData {:recipient {:id recipientId} :message {:text messageText}}]
      (println messageData)
      (sendAPI messageData)))

(defn sendImageMessage [[recipientId imageUrl]]
    (let [messageData {:recipient {:id recipientId}
                       :message {:attachment {:type "image"
                                              :payload {:url imageUrl}}}}]
      (println messageData)
      (sendAPI messageData)))

(defn sendQuickReply [[recipientId]]
    (let [messageData {:recipient {:id recipientId}
                       :message {:text "What's your favorite movie genre?"
                                 :quick_replies [{:content_type "text"
                                                  :title "Action"
                                                  :payload "DEVELOPER_DEFINED_PAYLOAD_FOR_PICKING_ACTION"}
                                                 {:content_type "text"
                                                  :title "Comedy"
                                                  :payload "DEVELOPER_DEFINED_PAYLOAD_FOR_PICKING_COMEDY"}
                                                 {:content_type "text"
                                                  :title "Drama"
                                                  :payload "DEVELOPER_DEFINED_PAYLOAD_FOR_PICKING_DRAMA"}]}}]
      (println messageData)
      (sendAPI messageData)))
