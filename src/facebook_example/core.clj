(ns facebook-example.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [facebook-example.facebook :as fb]
            [facebook-example.bot :as bot]
            ; Dependencies via Heroku Example
            [compojure.handler :refer [site]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [facebook-example.web]
            [facebook-example.repl :as repl]
            [clojure.tools.cli :as cli]))

(def opts-spec '(["-c" "--cli" "use the bot via CLI" :id :cli :default false]))

(defn is-cli? [opts]
  (get-in opts [:options :cli]))

(defn -main [& args]
  (let [opts (cli/parse-opts args opts-spec)]
    (if (is-cli? opts) (repl/run) (jetty/run-jetty facebook-example.web/app {:port 3000}))))
