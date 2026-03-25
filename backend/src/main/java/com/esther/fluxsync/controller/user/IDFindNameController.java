package com.esther.fluxsync.controller.user;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.service.DataBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
class IDFindNameController {

    private final DataBaseService db;
    public IDFindNameController(DataBaseService db) {
        this.db = db;
    }

    @RequestMapping("/find/{uid}")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> findUsername(@PathVariable int uid) {

        record rst(
                int uid,
                String username
        ) {}

        rst res = db.query(
                "SELECT username FROM fluxsync_user.users WHERE uid = ?;",
                rs -> new rst(
                        uid,
                        rs.getString("username")
                ),
                uid
        ).get(0);

        return new ResponseEntity<>(ApiResponse.success(res), HttpStatus.OK);

    }

}
