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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/channel")
class GetChannelController {

    private final DataBaseService db;
    public GetChannelController(DataBaseService db) {
        this.db = db;
    }

    @GetMapping("/get/{uid}")
    @UseDB("fluxsync")
    public ResponseEntity<ApiResponse<?>> getChannel(@PathVariable int uid) {

        record rst(
                int uid,
                String cid,
                String name,
                Timestamp jointime,
                Timestamp createtime,
                String description,
                String role
        ) {}

        List<rst> res = db.query(
                "SELECT cu.uid, cu.cid, cu.jointime, cu.role, c.name, c.createtime, c.description" +
                    " FROM fluxsync_channel.channel_user cu INNER JOIN fluxsync_channel.channel c ON cu.cid = c.cid" +
                    " WHERE cu.uid = ?;",
                rs -> new rst(
                        uid,
                        rs.getString("cid"),
                        rs.getString("name"),
                        rs.getTimestamp("jointime"),
                        rs.getTimestamp("createtime"),
                        rs.getString("description"),
                        rs.getString("role")
                ),
                uid
        );

        if (res.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error(404, "未找到数据"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ApiResponse.success(res), HttpStatus.OK);

    }

}
