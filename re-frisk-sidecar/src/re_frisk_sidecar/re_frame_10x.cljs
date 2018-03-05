(ns re-frisk-sidecar.re-frame-10x
  (:require day8.re-frame-10x.preload
            [mranderson047.re-frame.v0v10v2.re-frame.core :as rf]
            [taoensso.sente :as sente]
            [taoensso.sente.packers.transit :as sente-transit]))

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
    :trace/log (rf/dispatch [:epochs/receive-new-traces (second ?data)])
    :noop))

;SENTE ROUTER
(defonce router_ (atom nil))

(defn stop-router! []
  (when-let [stop-f @router_] (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router_
          (sente/start-client-chsk-router!
            ch-chsk event-msg-handler)))

(defn ^:export run [port]
  (rf/dispatch-sync [:settings/panel-width% 1])
  (rf/dispatch-sync [:settings/show-panel? true])
  (start-router!))

