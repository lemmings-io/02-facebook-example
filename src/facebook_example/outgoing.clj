(ns facebook-example.outgoing
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
   {:message (templates/image-message "https://upload.wikimedia.org/wikipedia/commons/e/ef/Tunturisopuli_Lemmus_Lemmus.jpg")}
   {:delay 1000}
   {:message (templates/button-template "Want to see the work of previous lemmings survivors?"
                                        [(templates/postback-button "Show them to me!" "GET_LEMMINGS_BOTS")])}])

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


(def lemmings-bots [{:title "Paul"
                     :subtitle "Sometimes you want to say things not explicitely, but in a more subtle way"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/14492618_1716720045320051_7494953960507444155_n.png?oh=09ceea61610c18012895d11b8fa2d7e4&oe=59C83992"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/sayitwithpaul")]}
                    {:title "Sauwetter"
                     :subtitle "Pictures of cute little piglets plus weather forecasts. Piglets!"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/14606441_215619388854252_4272343163239963228_n.jpg?oh=193e56eb423920df568604a1201fcf6c&oe=59CC587A"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/215618778854313")]}
                    {:title "Playing Savage"
                     :subtitle "The most initimate Messsenger experience for fans of Playing Savage"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/18557153_1939410499624186_7320643847071465324_n.jpg?oh=5fcab89d2da6df4680cc89ce8e804303&oe=59C36246"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/PlayingSavageMusic")]}
                    {:title "Donde"
                     :subtitle "Donde tells you how to get home with Wiener Linien"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/14666040_145368665925224_5645689795456166525_n.png?oh=073d985d002160dc70adfb99173811de&oe=59C8B7E5"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/dungeonsdice")]}
                    {:title "Dungeons & Dice"
                     :subtitle "Ever got eaten by a dragon because you left your roleplaying dies at home?"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/14657345_1321401907899832_4616460970271275030_n.png?oh=184a027c8b56264c59f4c6ee65da66a3&oe=5A0B6EE4"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/dungeonsdice")]}
                    {:title "Mimimi Bot"
                     :subtitle "Here to listen..."
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/17202873_651448481728515_4423169086552211658_n.png?oh=ed7ffa839663125f7959389b07c93914&oe=59C9E112"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/mimimibot")]}
                    {:title "Tsuki"
                     :subtitle "Sends you NASAs Astronomy Picture of the Day"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/17553876_419552861711667_4855657064273184377_n.jpg?oh=4b9439526e739123c2ba6aecae493f53&oe=59CC7726"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/satounotsuki")]}
                    {:title "Artemis, Artnapper"
                     :subtitle "The best buddy to identify and nap art with"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/14666029_141141623015535_2386849505368510199_n.jpg?oh=9fd56d02ef7c5e47ca5d206c1ad41dc9&oe=59D2BFCE"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/artemis.artnapper")]}
                    {:title "Comicbot"
                     :subtitle "Comicbot delivers the latest issues of your favourite webcomics"
                     :image_url "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/16649113_176576859498215_3890797700710252385_n.png?oh=984a045cd31e884c881c7a5b7555a3bb&oe=5A043372"
                     :buttons [(templates/url-button "give it a try!" "https://m.me/thecomicbot")]}])

(defn send-lemmings-bots []
  [{:message (templates/text-message "Let me show you some examples of what your predecessors created:")}
   {:message (templates/generic-template (shuffle lemmings-bots))}])
