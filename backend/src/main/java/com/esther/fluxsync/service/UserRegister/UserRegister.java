package com.esther.fluxsync.service.UserRegister;

import com.esther.fluxsync.model.dto.UserDTO;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegister {

    private final DataBaseService db;

    public UserRegister(DataBaseService db) {
        this.db = db;
    }

    @Transactional
    public void registerUser(UserDTO user, String secret) {

        db.update(
                "INSERT INTO fluxsync_user.users (username, password) VALUES (?, MD5(?))",
                user.getUsername(),
                user.getPassword()
        );

        db.update(
                "INSERT INTO fluxsync_user.totp_info (username, totp_secret) VALUES (?, ?)",
                user.getUsername(),
                secret
        );

        db.update(
          "INSERT INTO fluxsync_user.avatar (username, avatar) VALUES (?, \"cat\")",
                user.getUsername()
        );

    }
}
