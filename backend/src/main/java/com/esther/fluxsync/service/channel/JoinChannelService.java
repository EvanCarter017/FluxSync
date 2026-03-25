package com.esther.fluxsync.service.channel;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.service.DataBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JoinChannelService {

    private final DataBaseService db;

    public JoinChannelService(DataBaseService db) {
        this.db = db;
    }

    @Transactional
    @UseDB("fluxsync")
    public void joinChannel(int uid, String cid) {

        db.update(
                "INSERT INTO fluxsync_channel.channel_user (uid, cid) VALUES (?, ?);",
                uid, cid
        );

    }

}
