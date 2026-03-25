package com.esther.fluxsync.websocket.handlers;

import com.esther.fluxsync.model.dto.ws.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.WebSocketSession;

public interface MessageHandler {
    Message.MessageType type();
    void process(Message msg, @NotNull WebSocketSession session);
}
