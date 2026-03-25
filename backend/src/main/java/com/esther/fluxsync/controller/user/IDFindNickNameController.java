package com.esther.fluxsync.controller.user;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class IDFindNickNameController {

    private final DataBaseService db;
    public IDFindNickNameController(DataBaseService db) {
        this.db = db;
    }

    @RequestMapping("/nfind/{uid}")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> findUsername(@PathVariable int uid) {

        record rst(
                int uid,
                String nickname
        ) {}

        rst res = db.query(
                "SELECT nickname FROM fluxsync_user.users WHERE uid = ?;",
                rs -> new rst(
                        uid,
                        rs.getString("nickname")
                ),
                uid
        ).get(0);

        return new ResponseEntity<>(ApiResponse.success(res), HttpStatus.OK);

    }

}
