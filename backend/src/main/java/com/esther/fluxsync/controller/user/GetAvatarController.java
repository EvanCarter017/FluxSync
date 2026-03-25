package com.esther.fluxsync.controller.user;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.UserDTO;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
class GetAvatarController {

    private final DataBaseService db;

    public GetAvatarController(DataBaseService db) { this.db = db; }

    @GetMapping("/avatar/{username}")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> getAvatar(@PathVariable String username) {

        List<String> uri = db.query(
                "SELECT avatar FROM fluxsync_user.avatar WHERE username = ?;",
                rs -> rs.getString(1),
                username
        );

        record Res(String avatar) {}
        HttpStatus status;
        ApiResponse<Res> apiRes;
        if (uri.isEmpty()) {
            status = HttpStatus.NOT_FOUND;
            apiRes = ApiResponse.error(404, "未设置头像", new Res(null));
        } else {
            status = HttpStatus.OK;
            apiRes = ApiResponse.success("获取成功", new Res(uri.get(0)));
        }

        return new ResponseEntity<>(apiRes, status);
    }

}
