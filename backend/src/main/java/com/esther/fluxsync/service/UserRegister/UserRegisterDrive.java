package com.esther.fluxsync.service.UserRegister;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterDrive {

    private final DataBaseService db;

    public UserRegisterDrive(DataBaseService db) {
        this.db = db;
    }

    @Transactional
    @UseDB("fluxsync")
    public void registerUser_Drive(int uid) {
        db.update(
                "INSERT INTO fluxsync_minio.driveinfo (uid) VALUES (?);",
                uid
        );
    }

}
