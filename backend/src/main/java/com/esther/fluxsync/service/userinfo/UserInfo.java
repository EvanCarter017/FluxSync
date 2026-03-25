package com.esther.fluxsync.service.userinfo;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfo {

    private final DataBaseService db;

    public UserInfo(DataBaseService db) {
        this.db = db;
    }

    @Transactional
    @UseDB("fluxsync")
    public int uid(String username) {
        return db.query(
                "SELECT uid FROM fluxsync_user.users WHERE username = ?;",
                rs -> rs.getInt("uid"),
                username
        ).get(0);
    }

}
