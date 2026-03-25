package com.esther.fluxsync.service.userinfo;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.DriveBytesDTO;
import com.esther.fluxsync.model.dto.UserInfoDTO;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.utils.FormatBytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserInfo {

    private static final Logger log = LoggerFactory.getLogger(GetUserInfo.class);
    private final DataBaseService db;

    public GetUserInfo(DataBaseService db) {
        this.db = db;
    }

    @UseDB("fluxsync")
    public UserInfoDTO info(String username, DriveBytesDTO driveBytes) {

        List<UserInfoDTO> res = db.query(
                "SELECT uid, nickname, role, description FROM fluxsync_user.users WHERE username = ?;",
                rs -> UserInfoDTO.of(
                        rs.getInt("uid"),
                        username,
                        rs.getString("nickname"),
                        rs.getString("role"),
                        rs.getString("description"),
                        FormatBytes.conversion(driveBytes.usedBytes()),
                        FormatBytes.conversion(driveBytes.limitBytes()),
                        String.format("%d", driveBytes.usedBytes()),
                        String.format("%d", driveBytes.limitBytes())
                ),
                username
        );

        return res.get(0);

    }

}
