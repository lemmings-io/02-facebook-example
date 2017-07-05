(ns facebook-example.reaction
  (:gen-class)
  (:require [fb-messenger.templates :as templates]))

(defn some-image []
  [{:message (templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")}])

(defn echo [message-text]
  [{:message (templates/text-message message-text)}])

(defn welcome []
  [{:message (templates/text-message "Welcome, fellow lemming =)")}
   {:message (templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")}])

(defn error []
  [{:message (templates/text-message "Sorry, I didn't get that! :(")}])

(defn thank-for-attachment []
  [{:message (templates/text-message "Thank you for your attachment :)")}])

(defn help []
  [{:message (templates/quick-replies-message "What do you need help with?"
                                              [(templates/quick-reply "Clojure" "CLOJURE" "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Clojure_logo.svg/2000px-Clojure_logo.svg.png")
                                               (templates/quick-reply "Heroku" "HEROKU" "https://marketplace-cdn.atlassian.com/files/images/5922c67f-a338-470d-9979-eed82d54a2fa.png")])}])

(defn send-clojure-docs []
  [{:message (templates/text-message "Find Clojure docs here: https://clojuredocs.org/")}])

(defn send-heroku-instructions []
  [{:message (templates/text-message "Find Heroku instructions here: https://github.com/lemmings-io/02-facebook-example#deploying-to-heroku")}])
