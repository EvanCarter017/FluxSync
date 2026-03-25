package com.esther.fluxsync.controller;

import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.DriveBytesDTO;
import com.esther.fluxsync.model.dto.UserInfoDTO;
import com.esther.fluxsync.service.userinfo.GetDriveBytes;
import com.esther.fluxsync.service.userinfo.GetUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
class MeController {

    private final GetDriveBytes getDriveBytes;
    private final GetUserInfo getUserInfo;

    public MeController( GetDriveBytes getDriveBytes,
                        GetUserInfo getUserInfo) {
        this.getDriveBytes = getDriveBytes;
        this.getUserInfo = getUserInfo;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getUserInfo(@RequestHeader("X-User") String username) {

        DriveBytesDTO driveBytes = getDriveBytes.getDriveBytes(username);

        UserInfoDTO res = getUserInfo.info(username, driveBytes);

        return new ResponseEntity<>(ApiResponse.success("获取成功", res), HttpStatus.OK);

    }

}
