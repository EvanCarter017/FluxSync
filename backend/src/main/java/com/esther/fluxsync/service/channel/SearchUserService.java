package com.esther.fluxsync.service.channel;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchUserService {

    private final DataBaseService db;

    public SearchUserService(DataBaseService db) {
        this.db = db;
    }

    public record rst(
            int uid,
            String avatar,
            String nickname,
            String username,
            String role
    ) {}

    @UseDB("fluxsync")
    public List<rst> search(String cid) {

        String sql =
                "select c.uid, u.nickname, u.username, a.avatar, c.role " +
                "FROM fluxsync_channel.channel_user AS c " +
                "INNER JOIN fluxsync_user.users AS u " +
                "ON c.uid = u.uid " +
                "INNER JOIN fluxsync_user.avatar AS a " +
                "ON u.username = a.username " +
                "WHERE c.cid = ?;";

        return db.query(
                sql,
                rs -> new rst(
                        rs.getInt("uid"),
                        rs.getString("avatar"),
                        rs.getString("nickname"),
                        rs.getString("username"),
                        rs.getString("role")
                ),
                cid
        );

    }

}
