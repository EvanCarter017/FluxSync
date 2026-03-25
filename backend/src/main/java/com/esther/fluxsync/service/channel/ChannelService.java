package com.esther.fluxsync.service.channel;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.utils.LogConverter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.esther.fluxsync.model.dto.team.ChannelMessageDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@Service
public class ChannelService {

    private static final Logger log = LoggerFactory.getLogger(ChannelService.class);

    private final DataBaseService db;
    public ChannelService(DataBaseService db) {
        this.db = db;
    }

    @UseDB("fluxsync")
    // get max seq
    public long getMaxSeq(String cid) {

        Long res = -1L;

        try {
            res = db.query("SELECT MAX(seq) FROM fluxsync_channel.message WHERE cid = ?", rs -> rs.getLong("MAX(seq)"), cid).get(0);
        } catch (Exception e) {
            log.error(LogConverter.error("Get Channel Max Seq Error: {}"), e);
        }

        return Objects.requireNonNullElse(res, 0L);

    }

    @UseDB("fluxsync")
    public List<ChannelMessageDTO> getMessages(String cid, long seq) {

        String sql = "SELECT payload,seq FROM (" +
                     "(SELECT payload, seq FROM fluxsync_channel.message WHERE cid = ? AND seq <= ? ORDER BY seq DESC LIMIT 1000) " +
                     "UNION ALL " +
                     "(SELECT payload, seq FROM fluxsync_channel.message WHERE cid = ? AND seq > ? ORDER BY seq ASC LIMIT 1000)) " +
                     "AS result ORDER BY result.seq";

        Object[] params = new Object[]{cid, seq, cid, seq};

        return db.query(sql, rs -> ChannelMessageDTO.of(
                rs.getString("payload"),
                rs.getLong("seq")
        ), params);

    }

}
