(ns facebook-example.heroku
  (:require [environ.core :refer [env]]))

(def heroku-metadata {:app-name (env :heroku-app-name)
                      :release-created-at (env :heroku-release-created-at)})

(def base (str "https://" (get heroku-metadata :app-name) ".herokuapp.com"))
