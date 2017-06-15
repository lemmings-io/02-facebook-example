(ns facebook-example.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [fb-messenger.send :refer [handle-webhook]]
            [fb-messenger.auth :refer [authenticate]]
            [facebook-example.bot :as bot]
            ; Dependencies via Heroku Example
            [compojure.handler :refer [site]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello Lemming :)"})

(defroutes fb-routes
  (GET  "/"        [] (splash))
  (POST "/webhook" request
                   (try
                     (handle-webhook bot/handle-message request)
                     (catch Exception e (.printStackTrace e))
                     (finally {:status 200})))
  (GET  "/webhook" authenticate))

(def app
  (-> (wrap-defaults fb-routes api-defaults)
      (wrap-keyword-params)
      (wrap-json-params)))

(defn -main [& args]
  (println "Started up"))
