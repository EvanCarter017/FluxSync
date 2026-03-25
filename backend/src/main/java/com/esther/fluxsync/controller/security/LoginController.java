package com.esther.fluxsync.controller.security;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.UserDTO;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/security")
class LoginController {

    private final DataBaseService db;

    public LoginController(DataBaseService db) {
        this.db = db;
    }

    @Value("${app.services.jwt.url}")
    public String jwtUrl;

    @Value("${app.services.jwt.uri}")
    public String jwtUri;

    // 登录接口
    @PostMapping("/login")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody UserDTO user) {

        List<Integer> userList = db.query(
                "SELECT * FROM fluxsync_user.users WHERE username = ? and password = MD5(?);",
                rs -> rs.getInt(1),
                user.getUsername(),
                user.getPassword()
        );

        if (userList.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error(401,"用户名或密码不正确."), HttpStatus.UNAUTHORIZED);
        }

        WebClient wc = WebClient.create(jwtUrl);
        record Jwt(
                String access_token,
                String token_type,
                String expires_in,
                String refresh_token,
                String refresh_expires_in
        ) {}
        Jwt token = wc.post()
                .uri(jwtUri)
                .bodyValue(new Object(){
                    public String getUsername() {return user.getUsername();}
                })
                .retrieve()
                .bodyToMono(Jwt.class)
                .switchIfEmpty(Mono.empty())
                .block();

        if (token == null) {
            return new ResponseEntity<>(ApiResponse.error(500,"登录失败."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        boolean totpEnable = db.query(
                "SELECT totp_enable FROM fluxsync_user.totp_info WHERE username = ?",
                rs -> rs.getBoolean(1),
                user.getUsername()
        ).get(0);


        record res(Jwt token, boolean totpEnable) {}
        return new ResponseEntity<>(ApiResponse.error(200,"登录成功.", new res(token, totpEnable)), HttpStatus.OK);
    }


}
