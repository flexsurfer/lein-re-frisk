(ns re-frisk-sidecar.core
  (:use compojure.core)
  (:require
    [org.httpkit.server :as ohs]
    [compojure.route :as route]
    [compojure.handler :as handler]
    [taoensso.sente :as sente]
    [taoensso.sente.packers.transit :as sente-transit]
    [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.util.response :as response]))

(let [{:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket-server!
        (get-sch-adapter) {:packer (sente-transit/get-transit-packer)})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom

;HANDLERS
(defmulti -event-msg-handler "Multimethod to handle Sente `event-msg`s" :id)

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]))

;RE-FRISK HANDLERS
(defmethod -event-msg-handler :refrisk/app-db
  [{:as ev-msg :keys [?reply-fn ?data]}]
  (let [uids (:any @connected-uids)]
    (doseq [uid uids]
      (chsk-send! uid [:refrisk/app-db ?data]))))

(defmethod -event-msg-handler :refrisk/events
  [{:as ev-msg :keys [?reply-fn ?data]}]
  (let [uids (:any @connected-uids)]
    (doseq [uid uids]
      (chsk-send! uid [:refrisk/events ?data]))))

;SENTE ROUTER
(defonce router_ (atom nil))

(defn stop-router! []
  (when-let [stop-fn @router_] (stop-fn)))

(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-server-chsk-router!
            ch-chsk event-msg-handler)))
;ROUTES
(defroutes
  app-routes
  (GET "/" req (response/content-type
                 (response/resource-response "public/index.html")
                 "text/html"))
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req))
  (route/resources "/")
  (route/not-found "Not Found"))

;ENTRY POINT
(defn -main [& [port]]
  (start-router!)
  (let [port' (Integer/parseInt (or port "4567"))]
    (ohs/run-server (-> app-routes
                        (wrap-defaults site-defaults)
                        (wrap-cors
                          :access-control-allow-origin #".*"
                          :access-control-allow-methods [:get :put :post :delete]
                          :access-control-allow-credentials "true"))
                    {:port port'})
    (println (str "re-frisk server has been started at http://localhost:" port'))))

(comment (-main)) ; removing warning in IDEA
