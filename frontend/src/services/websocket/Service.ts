import config from "@/store";
import {computed, ref} from "vue";
// const url = "ws://localhost:1007/api/ws";
import Leader from "@/services/channel/channel";
import jsCookie from "js-cookie";

let ws = null;
let ping = null;

const initWebSocket = (url, token) => {
    if (ws) return ws;
    if (!token) throw new Error("[FluxSync] No token provided.");

    ws = new WebSocket(`${url}?Authorization=Bearer ${token}`);

    ws.onopen = () => {
        console.log("[FluxSync] 服务器连接成功.");
        readyState.value = WebSocket.OPEN;
    };

    ws.onmessage = (event) => {
        if (event.data === "Connected") {
            send({
                type: "join",
                sid: config.info.uid,
                timestamp: Date.now()
            });
            ping = setInterval(() => {
                send({
                    type: "ping",
                    sid: config.info.uid,
                    timestamp: Date.now()
                });
            }, 30000);
            return ;
        }
        let data = null;
        try {
            data = JSON.parse(event.data);
        } catch (err) {
            data = event.data;
        }
        console.log("[FluxSync] Received:", data);
    };

    ws.onclose = () => {
        clearInterval(ping);
        console.log("[FluxSync] 服务已关闭.");
        readyState.value = WebSocket.CLOSED;
        Leader.declareOffline();
    };

    ws.onerror = (err) => {
        console.error("[FluxSync] 服务器连接错误:", err);
        readyState.value = WebSocket.CLOSED;
    };

    return ws;

}

const send = (data) => {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        console.warn("[FluxSync] 服务未连接.");
        if (Leader.isLeader) {
            initWebSocket("ws://localhost:1007/api/ws", jsCookie.get('jwt-at'));
        }
        return ;
    }
    const message = typeof data === "string" ? data : JSON.stringify(data);
    ws.send(message);
}

const close = () => {
    if (ws) {
        ws.close();
        ws = null;
    }
}

const get = () => {
    return ws;
}

const readyState = ref(WebSocket.CLOSED);
const ready = computed(() => readyState.value === WebSocket.OPEN);

export default {
    initWebSocket,
    send,
    close,
    get,
    ready
}