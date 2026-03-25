package com.esther.fluxsync.websocket.handlers;

import com.esther.fluxsync.model.dto.ws.Message;
import com.esther.fluxsync.service.RedisService;
import com.esther.fluxsync.utils.LogConverter;
import com.esther.fluxsync.utils.RedisKeysConverter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;

@Component
public class PINGHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(PINGHandler.class);

    private final RedisService redis;

    public PINGHandler(RedisService redis) {
        this.redis = redis;
    }

    @Override
    public Message.MessageType type() {
        return Message.MessageType.PING;
    }

    @Override
    public void process(Message message, @NotNull WebSocketSession session) {

        int uid = message.sid();
        String key = RedisKeysConverter.user(uid, session.getId());

        boolean ok = redis.expire(key, Duration.ofMinutes(2));

        if (!ok) {
            log.warn(LogConverter.warn("(@websocket:ping) 未能刷新Redis过期时间, key={}"), key);
        } else {
            log.debug(LogConverter.debug("(@websocket:ping) 用户 key = <{}>, 已续时"), key);
        }

    }

}
