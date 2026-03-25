package com.esther.fluxsync.controller.security;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.UserDTO;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.service.TotpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/security/totp")
class TotpController {

    private final DataBaseService db;
    private final TotpService totp;

    public TotpController(DataBaseService db,
                          TotpService totp) {
        this.db = db;
        this.totp = totp;
    }

    // totp校验接口
    @PostMapping("/verify")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> verifyTotp(@RequestBody UserDTO user) {

        String key = db.query(
            "SELECT totp_secret FROM fluxsync_user.totp_info WHERE username = ?;",
                rs -> rs.getString(1),
                user.getUsername()
        ).get(0);

        if (totp.verifyCode(key, user.getCode())) {
            return new ResponseEntity<>(ApiResponse.success("校验通过"), HttpStatus.OK);
        }

        return new ResponseEntity<>(ApiResponse.error(401, "校验失败"), HttpStatus.UNAUTHORIZED);

    }

    // 生成totp uri image
    @PostMapping("/img")
    @UseDB("fluxsync")
    public ResponseEntity<?> getTotpUri(@RequestBody UserDTO user) {

        String key = db.query(
                "SELECT totp_secret FROM fluxsync_user.totp_info WHERE username = ?;",
                rs -> rs.getString(1),
                user.getUsername()
        ).get(0);

        byte[] img = totp.createTotpQrCode(totp.buildAuthObject(key), user.getUsername());

        if (img == null) {
            return new ResponseEntity<>(ApiResponse.error(503, "生成失败"), HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(img);

    }

}
