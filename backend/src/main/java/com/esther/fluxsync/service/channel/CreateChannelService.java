package com.esther.fluxsync.service.channel;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateChannelService {

    private final DataBaseService db;

    public CreateChannelService(DataBaseService db) {
        this.db = db;
    }

    @Transactional
    @UseDB("fluxsync")
    public void createChannel(int uid, String cid, String name, String description) {

        db.update(
                "INSERT INTO fluxsync_channel.channel (cid, name, description) VALUES (?, ?, ?);",
                cid, name, description
        );

        db.update(
                "INSERT INTO fluxsync_channel.channel_user (uid, cid, role) VALUES (?, ?, ?);",
                uid, cid, "creator"
        );

    }

}
