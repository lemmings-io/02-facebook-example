(ns facebook-example.reaction
  (:gen-class)
  (:require [fb-messenger.templates :as templates]))

; You can use three kind of replies: actions, messages and delay.

; Find the documentation for actions here:
; https://developers.facebook.com/docs/messenger-platform/send-api-reference/sender-actions

; Messages are not only text messages
; https://developers.facebook.com/docs/messenger-platform/send-api-reference/text-message
; but also more complex UI elements supported by the FB Messenger API, like quick-replies
; https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
; or templates https://developers.facebook.com/docs/messenger-platform/send-api-reference/templates

; EXAMPLES

; If you want your bot to keep typing for 3 seconds, write this:
; [{:action "typing_on"}
;  {:delay 3000}]

; If you want your bot to see a message and reply after 2 seconds, write this:
; [{:action "mark_seen"}
;  {:delay 2000}
;  {:message (template/text-message "Alright!")}]

(defn some-image []
  [{:message (templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")}])

(defn echo [message-text]
  [{:message (templates/text-message message-text)}])

(defn welcome []
  [{:action "typing_on"}
   {:delay 3000}
   {:message (templates/text-message "Welcome, fellow lemming =)")}
   {:message (templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")}])

(defn error []
  [{:message (templates/text-message "Sorry, I didn't get that! :(")}])

(defn thank-for-attachment []
  [{:delay 3000}
   {:action "mark_seen"}
   {:delay 3000}
   {:action "typing_on"}
   {:delay 3000}
   {:message (templates/text-message "Thank you for your attachment :)")}])

(defn help []
  [{:message (templates/quick-replies-message "What do you need help with?"
                                              [(templates/quick-reply "Clojure" "CLOJURE" "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Clojure_logo.svg/2000px-Clojure_logo.svg.png")
                                               (templates/quick-reply "Heroku" "HEROKU" "https://marketplace-cdn.atlassian.com/files/images/5922c67f-a338-470d-9979-eed82d54a2fa.png")])}])

(defn send-clojure-docs []
  [{:message (templates/text-message "Find Clojure docs here: https://clojuredocs.org/")}])

(defn send-heroku-instructions []
  [{:message (templates/text-message "Find Heroku instructions here: https://github.com/lemmings-io/02-facebook-example#deploying-to-heroku")}])
