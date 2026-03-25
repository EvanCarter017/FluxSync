package com.esther.fluxsync.model.dto;

import com.esther.fluxsync.service.userinfo.UserInfo;

public record UserInfoDTO(
        int uid,
        String username,
        String nickname,
        String role,
        String description,
        String usedBytes,
        String limitBytes,
        String usedBytesNumber,
        String limitBytesNumber
) {

    public static UserInfoDTO of(
            int uid,
            String username,
            String nickname,
            String role,
            String description,
            String usedBytes,
            String limitBytes,
            String usedBytesNumber,
            String limitBytesNumber
    ) {
        return new UserInfoDTO(
            uid, username, nickname, role, description, usedBytes, limitBytes, usedBytesNumber, limitBytesNumber
        );
    }

    public static UserInfoDTO empty() {
        return new UserInfoDTO(0, null, null, null, null, null, null, null, null);
    }

}