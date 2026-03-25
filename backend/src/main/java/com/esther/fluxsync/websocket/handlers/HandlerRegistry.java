package com.esther.fluxsync.websocket.handlers;

import com.esther.fluxsync.model.dto.ws.Message;
import com.esther.fluxsync.utils.LogConverter;
import com.esther.fluxsync.websocket.RedisExpiredListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class HandlerRegistry {

    private static final Logger log = LoggerFactory.getLogger(HandlerRegistry.class);

    private final Map<Message.MessageType, MessageHandler> HANDLERS = new EnumMap<>(Message.MessageType.class);

    public HandlerRegistry(List<MessageHandler> handlers) {
        for (MessageHandler handler : handlers) {
            HANDLERS.put(handler.type(), handler);
        }
    }

    public void process(Message msg, @NotNull WebSocketSession session) {
        MessageHandler handler = HANDLERS.get(msg.type());
        if (handler != null) {
            handler.process(msg, session);
        } else {
            log.warn(LogConverter.warn("(@WebsocketHandlerRegistry) 执行器 {} 未找到."), msg.type());
        }
    }

}
