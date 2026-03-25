package com.esther.fluxsync.model.dto.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record Message(

        // 消息类型
        MessageType type,

        // 来源者 id
        int sid,

        // 载荷
        JsonNode payload,

        // 发起时间 (毫秒级时间戳)
        long timestamp

) {
    public static enum MessageType {
        // 频道聊天事件
        CHAT,
        // 心跳包
        PING,
        // 握手事件 (初次连接)
        JOIN,
        // 离开频道事件
        LEAVE,
        // 协作更新事件 (CRDT)
        EDIT;

        @JsonCreator
        public static MessageType from(String value) {
            return MessageType.valueOf(value.toUpperCase());
        }

        @JsonValue
        public String toValue() {
            return name().toLowerCase();
        }
    }
}
