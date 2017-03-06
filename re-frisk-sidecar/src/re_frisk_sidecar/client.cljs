(ns re-frisk-sidecar.client
  (:require
    [reagent.core :as reagent]
    [taoensso.sente  :as sente]
    [taoensso.sente.packers.transit :as sente-transit]
    [re-frisk-shell.core :as ui])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defonce app-db (reagent/atom 1))
(defonce re-frame-data (reagent/atom {:app-db (reaction @app-db)}))
(defonce re-frame-events (reagent/atom []))
(defonce deb-data (reagent/atom {}))

(defn update-app-db [val]
  (reset! app-db val))

(defn update-events [val]
  (swap! re-frame-events conj val))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket-client!
        "/chsk" ; Must match server Ring routing URL
        {:type   :auto
         :host   (str "localhost:" js/location.port)
         :packer (sente-transit/get-transit-packer)})]
  (def ch-chsk ch-recv)) ; ChannelSocket's receive channel

;SENTE HANDLERS
(defmulti -event-msg-handler "Multimethod to handle Sente `event-msg`s" :id)

(defn event-msg-handler
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler :default [_])

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (case (first ?data)
        :refrisk/app-db (update-app-db (second ?data))
        :refrisk/events (update-events (second ?data))))

;SENTE ROUTER
(defonce router_ (atom nil))

(defn stop-router! []
  (when-let [stop-f @router_] (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-client-chsk-router!
            ch-chsk event-msg-handler)))

;REAGENT RENDER
(defn mount []
  (reagent/render [ui/debugger-shell re-frame-data re-frame-events deb-data]
                  (.getElementById js/document "app")))

;ENTRY POINT
(defn ^:export run [port]
  (start-router!)
  (mount))

(defn on-js-reload []
  (mount))

(comment (on-js-reload) (run)) ; removing warning in IDEA


