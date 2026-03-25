package com.esther.fluxsync.service.userinfo;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.DriveBytesDTO;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;

@Service
public class GetDriveBytes {

    private final DataBaseService db;
    private final UserInfo userInfo;

    public GetDriveBytes(DataBaseService db,
                        UserInfo userInfo) {
        this.db = db;
        this.userInfo = userInfo;
    }

    @UseDB("fluxsync")
    public DriveBytesDTO getDriveBytes(String username) {

        return db.query(
                "SELECT usedBytes, limitBytes FROM fluxsync_minio.driveinfo WHERE uid = ?;",
                rs -> DriveBytesDTO.of(
                        rs.getLong("usedBytes"),
                        rs.getLong("limitBytes")
                ),
                userInfo.uid(username)
        ).get(0);
    }

}
