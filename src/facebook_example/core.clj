(ns facebook-example.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [fb-messenger.webhook :refer [handle-events]]
            [fb-messenger.auth]
            [fb-messenger.send]
            ; Dependencies via Heroku Example
            [compojure.handler :refer [site]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.core.async :as async]
            [clojure.core.match :refer [match]]
            [clojure.spec.alpha :as spec]
            [clojure.future :refer :all]
            [fb-messenger.send :as facebook]
            [facebook-example.incoming :refer [on-quick-reply on-attachments
                                               on-message on-postback]]))

(fb-messenger.send/set-page-access-token! (env :page-access-token))
(fb-messenger.auth/set-token! (env :verify-token))

; SCHEMA FOR REPLIES
(spec/def ::action #{"typing_on" "typing_off" "mark_seen"})
(spec/def ::delay int?)
(spec/def ::message map?)

(spec/def ::message-reply (spec/keys :req-un [::message]))
(spec/def ::action-reply (spec/keys :req-un [::action]))
(spec/def ::delay-reply (spec/keys :req-un [::delay]))

(spec/def ::reply (spec/or :message-reply ::message-reply
                           :action-reply ::action-reply
                           :delay-reply ::delay-reply))

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
      (when-not (spec/valid? ::reply reply)
        (throw (ex-info "The spec check has failed. Please make sure you are sending a correct reply pattern."
                        {:causes (spec/explain-str ::reply reply)})))

      ; MATCH BOT REPLY
      ; The order of the matching clauses is important!
      (match [reply]

        ; The bot wants to send a message (text, images, videos etc.)
        [{:message message}]
        (facebook/send-message sender-id message)

        ; The bot wants to perform an action (mark_seen, typing_on, typing_off)
        [{:action action}]
        (facebook/send-sender-action sender-id action)

        ; The bot wants to wait n milliseconds
        [{:delay delay}]
        (Thread/sleep delay)

        :else
        (throw (ex-info "You have provided an invalid pattern in your reply."
                        {:causes reply}))))))

; SERVER
(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello Lemming :)"})

(defroutes fb-routes
  (GET  "/"        [] (splash))
  (POST "/webhook" request
    (async/go
       (try
         (handle-events handle-message request)
         (catch Exception e (.printStackTrace e))))
    {:status 200})
  (GET "/webhook" request
    ; fb-messenger.auth/authenticate expects the query-params map and inspects
    ; the fb params, if the token is correct it returns the challenge, otherwise
    ; nil.
    ; We check if it returned nil, and change the status to 403 in this case.
    ; If it returns the challenge we just pass it through to Facebook, so that
    ; the verification succeeds.
    (let [result (fb-messenger.auth/authenticate (get request :query-params))]
      (if result
        result
        {:status 403}))))

(def app
  (-> (wrap-defaults fb-routes api-defaults)
      (wrap-keyword-params)
      (wrap-json-params)))

(defn -main [& args]
  (jetty/run-jetty app {:port (read-string (or (env :port) "3000"))}))


