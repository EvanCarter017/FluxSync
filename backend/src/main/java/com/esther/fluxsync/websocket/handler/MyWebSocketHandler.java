package com.esther.fluxsync.websocket.handler;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ws.Message;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.service.RedisService;
import com.esther.fluxsync.utils.LogConverter;
import com.esther.fluxsync.utils.RedisKeysConverter;
import com.esther.fluxsync.websocket.handlers.HandlerRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MyWebSocketHandler.class);

    private final RedisService redisService;
    private final HandlerRegistry handlers;
    private final DataBaseService dbService;

    private static RedisService redis;
    private static DataBaseService db;


    public MyWebSocketHandler(RedisService redisService,
                              HandlerRegistry handlers,
                              DataBaseService dbService) {
        this.redisService = redisService;
        this.handlers = handlers;
        this.dbService = dbService;
        redis = redisService;
        db = dbService;
    }

    // 存储在线用户：用户ID -> WebSocket会话
    private static final ConcurrentHashMap<Integer, WebSocketSession> users = new ConcurrentHashMap<>();

    // 存储频道：频道ID -> 用户ID列表
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> channels = new ConcurrentHashMap<>();

    // 存储临时用户：会话ID -> WebSocket会话
    private static final ConcurrentHashMap<String, WebSocketSession> tempUser = new ConcurrentHashMap<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 清理超时未注册会话
     */
    public static void removeTempUser(String sessionId) throws IOException {
        WebSocketSession session = tempUser.remove(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage("Registration timeout."));
                session.close();
            } catch (IOException e) {
                log.warn(LogConverter.warn("(@WebSocketHandler) Session {} send message error."), sessionId);
            }
        } else {
            log.warn(LogConverter.warn("(@WebSocketHandler) Session {} already closed or not found."), sessionId);
        }
    }

    /**
     * 清理超时在线用户
     */
    @UseDB("fluxsync")
    public static void removeUser(int uid, String sessionId) throws IOException {

        WebSocketSession session = users.remove(uid);

        List<String> cids = db.query(
                "SELECT * FROM fluxsync_channel.channel_user WHERE uid = ?;",
                rs -> rs.getString("cid"),
                uid
        );

        if (session != null && session.isOpen()) {

            cids.forEach(item -> {
                channels.get(item).remove(uid);
            });

            try {
                session.sendMessage(new TextMessage("{\"message\": \"ping timeout.\"}"));
                session.close();
            } catch (IOException e) {
                log.warn(LogConverter.warn("(@WebSocketHandler) Session {} already closed or not found."), sessionId);
            }

        } else {
            log.warn(LogConverter.warn("(@WebSocketHandler) Session {} already closed or not found."), sessionId);
        }

    }

    /**
     * 注册用户
     */
    public static void registryUser(@NotNull WebSocketSession session, int sid, List<String> cids) {
        tempUser.remove(session.getId(), session);
        redis.delete(RedisKeysConverter.tempUser(session.getId()));

        if (users.containsKey(sid)) {
            log.info(LogConverter.info("(@websocket) 用户 {} 重连."), sid);
            String oldSessionId = users.get(sid).getId();
            redis.delete(RedisKeysConverter.user(sid, oldSessionId));
            users.put(sid, session);
            redis.save(RedisKeysConverter.user(sid, session.getId()), 1, Duration.ofMinutes(2));
        } else {
            users.put(sid, session);
            redis.save(RedisKeysConverter.user(sid, session.getId()), 1, Duration.ofMinutes(2));
            cids.forEach(item -> {
                channels.computeIfAbsent(item, k -> new CopyOnWriteArrayList<>()).add(sid);
            });
        }

    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        tempUser.put(session.getId(), session);
        redisService.save("ws:user:temp:" + session.getId(), "1", Duration.ofSeconds(30));
        session.sendMessage(new TextMessage("Connected"));
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Message msg = mapper.readValue(payload, Message.class);
        handlers.process(msg, session);

//        System.out.println(msg.payload().get("test").asText());
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        log.debug(LogConverter.debug("(@WebSocketHandler) 用户 {} 断开连接."), session.getId());
//        redis.delete(RedisKeysConverter.user(sid, session.getId()));
        redis.dimDelete("ws:user:*:"+session.getId());
        session.close();
//        users.remove(session.getId());
//        session.close();
//        System.out.println("🔴 客户端 " + session.getId() + " 断开连接");
    }


}
