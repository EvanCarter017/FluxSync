package com.esther.fluxsync.websocket.handlers;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ws.Message;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.utils.SmartIdUtil;
import com.esther.fluxsync.websocket.handler.MyWebSocketHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class JOINHandler implements MessageHandler {

    private final DataBaseService db;

    public JOINHandler(DataBaseService db) {
        this.db = db;
    }

    @Override
    public Message.MessageType type() {
        return Message.MessageType.JOIN;
    }

    @Override
    @UseDB("fluxsync")
    public void process(Message msg, @NotNull WebSocketSession session) {

        int sid = msg.sid();
        long timestamp = msg.timestamp();

        List<String> cids = db.query(
                "SELECT * FROM fluxsync_channel.channel_user WHERE uid = ?;",
                rs -> rs.getString("cid"),
                sid
        );

        MyWebSocketHandler.registryUser(session, sid, cids);

    }

}
