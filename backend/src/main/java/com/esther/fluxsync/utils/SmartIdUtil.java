package com.esther.fluxsync.utils;


import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class SmartIdUtil {

    private SmartIdUtil() {}

    /** 随机数生成器 */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 时间周期 194 days */
    private static final long CYCLE_SECONDS = 16_777_216L;

    /** 时间种子 */
    private static final long BASE_EPOCH = LocalDateTime.of(2025, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

    /** 时间偏移周期 64 days */
    private static final long OFFSET_PERIOD = CYCLE_SECONDS / 3;

    // 生成短ID
    public static String generate() {
        // 当前秒级时间戳
        long nowSec = Instant.now().getEpochSecond();

        // 计算 epoch 偏移
        long epochOffset = (nowSec - BASE_EPOCH) / OFFSET_PERIOD;

        // 生成逻辑时间
        long logicalTime = ((nowSec - BASE_EPOCH) + (epochOffset * 9973)) & 0xFFFFFF;

        // 时间高位 (6 hex) + 随机 (4 hex)
        return String.format("%06X%04X", logicalTime & 0xFFFFFF, RANDOM.nextInt(0x10000));

    }

}
