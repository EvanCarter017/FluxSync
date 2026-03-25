package com.esther.fluxsync.websocket.handlers;

import com.esther.fluxsync.model.dto.ws.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LEAVEHandler implements MessageHandler {

    @Override
    public Message.MessageType type() {
        return Message.MessageType.LEAVE;
    }

    @Override
    public void process(Message message, @NotNull WebSocketSession session) {
        System.out.println("CHAT");
    }

}
