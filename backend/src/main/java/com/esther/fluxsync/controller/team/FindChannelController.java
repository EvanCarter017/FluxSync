package com.esther.fluxsync.controller.team;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/channel")
class FindChannelController {

    private final DataBaseService db;
    public FindChannelController(DataBaseService db) {
        this.db = db;
    }

    @GetMapping("/find/{cid}")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> getAll(@PathVariable String cid) {

        record rst(
                int uid,
                String cid,
                Timestamp jointime,
                String role
        ) {}

        List<rst> res = db.query(
                "SELECT * FROM fluxsync_channel.channel_user WHERE cid = ?;",
                rs -> new rst(
                        rs.getInt("uid"),
                        cid,
                        rs.getTimestamp("jointime"),
                        rs.getString("role")
                ),
                cid
        );

        if (res.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error(404, "未找到数据"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ApiResponse.success(res), HttpStatus.OK);

    }

}
