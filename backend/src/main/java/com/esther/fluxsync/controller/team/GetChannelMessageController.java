package com.esther.fluxsync.controller.team;

import com.esther.fluxsync.ds.UseDB;
import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.service.DataBaseService;
import com.esther.fluxsync.service.RedisService;
import com.esther.fluxsync.service.channel.ChannelService;
import com.esther.fluxsync.utils.RedisKeysConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.esther.fluxsync.model.dto.team.ChannelMessageDTO;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/channel")
class GetChannelMessageController {

    private final DataBaseService db;
    private final RedisService redis;
    private final ChannelService channel;
    public GetChannelMessageController(DataBaseService db, RedisService redis, ChannelService channel) {
        this.db = db;
        this.redis = redis;
        this.channel = channel;
    }

    @GetMapping("/messages")
    @UseDB("fluxsync")
    // get channel messages -> response.status[200, 503]
    public ResponseEntity<ApiResponse<?>> getMessage(@RequestParam String cid, @RequestParam int seq) {

        final String key = RedisKeysConverter.chSeq(cid);
        final long maxSeq = channel.getMaxSeq(cid);
        final long seqNoneMessageState = -1L;

        // get max seq error -> 503
        if (redis.get(key) == null) {
            if (maxSeq == seqNoneMessageState) return new ResponseEntity<>(ApiResponse.error("service error."), HttpStatus.SERVICE_UNAVAILABLE);
            redis.savenx(key, maxSeq);
        }

        // get messages
        List<ChannelMessageDTO> messages = channel.getMessages(cid, seq == -1 ? maxSeq : seq);

        // response dto
        record res(
                int length,
                List<ChannelMessageDTO> messages
        ) {}

        // return response 200 + body
        return new ResponseEntity<>(ApiResponse.success(new res(messages.size(),messages)), HttpStatus.OK);

    }

}
