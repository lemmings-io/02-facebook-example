(ns facebook-example.api.vision
  (:gen-class)
  (:require [clojure.string :as s]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]])
  (import (org.apache.commons.codec.binary Base64)))

(def VISION-KEY (env :vision-key))

;;; (download-image url) downloads an url as a byte array
(defn download-image [url]
  (let [response (http/get url {:as :byte-array})]
    (:body @response)))

;;; (image-to-b64 image) base64 encodes a image byte array
(defn image-to-b64 [image]
  (String. (Base64/encodeBase64 image)))

;;; (request-body b64-image) generates a request body for the given b64 encoded image
;;; for description of FACE_DETECTION & LABEL_DETECTION check out:
;;; https://cloud.google.com/vision/docs/detecting-faces#vision-face-detection-protocol
;;; https://cloud.google.com/vision/docs/detecting-labels#vision-label-detection-protocol
;;; for other available features check out:
;;; https://cloud.google.com/vision/docs/how-to
;;; eg:
;;; {"type" "IMAGE_PROPERTIES"}
;;; adds :imagePropertiesAnnotation to response (see bot.clj:37)
(defn request-body [b64-image]
  {"requests" [{"image" {"content" b64-image}
                "features" [{"type" "FACE_DETECTION"}
                            {"type" "LABEL_DETECTION"}]}]})

;;; (call-vision-api (b64-image) sends a generated request to the GC Vision API
;;; see https://cloud.google.com/vision/docs/how-to for features
;;; and corresponding responses
(defn call-vision-api [b64-image]
    (as-> b64-image data
          @(http/post "https://vision.googleapis.com/v1/images:annotate"
                     {  :query-params {"key" VISION-KEY}
                        :headers {"Content-Type" "application/json"}
                        :body (json/write-str (request-body data))
                        :insecure? true})))

;;; (handle-vision-response @response) handles the response from GC Vision API
(defn handle-vision-response [{:keys [status headers body error]}]
  (if (= status 200)
      (-> (json/read-str body :key-fn keyword)
          :responses
          first)
      (do (println "ERROR: Call to Vision API failed. Maybe check your vision-key in profiles.clj?")
          (println body))))

;;; (analyze url) takes an image url and returns a response from GC Vision API
(defn analyze [url]
  (-> (download-image url)
      image-to-b64
      call-vision-api
      handle-vision-response))
