package com.esther.fluxsync.websocket;

import com.esther.fluxsync.service.RedisService;
import com.esther.fluxsync.utils.LogConverter;
import com.esther.fluxsync.websocket.handler.MyWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RedisExpiredListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RedisExpiredListener.class);

    private final RedisService redisService;

    public RedisExpiredListener(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("ws:user:temp:")) {

            String sessionId = expiredKey.substring("ws:user:temp:".length());
            try {
                MyWebSocketHandler.removeTempUser(sessionId);
            } catch (Exception e) {
                log.warn(LogConverter.warn("(@RedisExpiredListener) Failed to remove temp user for session {}: {}"), sessionId, e.getMessage());
            }

            log.warn(LogConverter.warn("(@RedisExpiredListener) 临时用户注册超时: {}"), sessionId);

        } else if (expiredKey.startsWith("ws:user:")) {
            int userId = Integer.parseInt(expiredKey.split(":")[2]);
            String sessionId = expiredKey.split(":")[3];

            try {
                MyWebSocketHandler.removeUser(userId, sessionId);
            } catch (Exception e) {
                log.warn(LogConverter.warn("(@RedisExpiredListener) User {} has been disconnected"), userId);
            }
        }
    }

}
