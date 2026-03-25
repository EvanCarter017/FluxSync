package com.esther.fluxsync.model.dto;

public record DriveBytesDTO(
        long usedBytes,
        long limitBytes
) {
    public static DriveBytesDTO of(long usedBytes, long limitBytes) {
        return new DriveBytesDTO(usedBytes, limitBytes);
    }

    public static DriveBytesDTO empty() {
        return new DriveBytesDTO(0, 0);
    }
}
