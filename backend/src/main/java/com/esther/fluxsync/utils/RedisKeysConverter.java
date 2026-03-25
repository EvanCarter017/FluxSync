package com.esther.fluxsync.utils;

public final class RedisKeysConverter {

    private RedisKeysConverter() {}

    // Temp Users Redis-Key Converter
    public static String tempUser(String sessionId) {
        return String.format("ws:user:temp:%s", sessionId);
    }

    // Users Redis-Key Converter
    public static String user(int uid, String sessionId) {
        return String.format("ws:user:%d:%s", uid, sessionId);
    }

    // channel Seq Key Converter
    public static String chSeq(String cid) {
        return String.format("ch:%s:seq", cid);
    }

}
