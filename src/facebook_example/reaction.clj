(ns facebook-example.reaction
  (:gen-class)
  (:require [fb-messenger.templates :as templates]))

(defn help []
  [(templates/text-message "Hi there, happy to help :)")])

(defn some-image []
  [(templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")])

(defn echo [message-text]
  [(templates/text-message message-text)])

(defn welcome []
  [(templates/text-message "Welcome, fellow lemming =)")
   (templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")])

(defn error []
  [(templates/text-message "Sorry, I didn't get that! :(")])

(defn thank-for-attachment []
  [(templates/text-message "Thank you for your attachment")])

(defn directions [coordinates]
  [(templates/text-message "Here are your directions")
   (templates/text-message (str "https://google.com/maps/dir/" (:lat coordinates) "," (:long coordinates) "/48.190870,16.318560"))])
