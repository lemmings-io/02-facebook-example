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
            [facebook-example.bot-core :refer [handle-message]]
            ; Dependencies via Heroku Example
            [compojure.handler :refer [site]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.core.async :as async]))

(fb-messenger.send/set-page-access-token! (env :page-access-token))
(fb-messenger.auth/set-token! (env :verify-token))

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
