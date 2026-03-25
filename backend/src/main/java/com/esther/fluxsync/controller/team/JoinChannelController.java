package com.esther.fluxsync.controller.team;

import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.ChannelDTO;
import com.esther.fluxsync.service.channel.CreateChannelService;
import com.esther.fluxsync.service.channel.JoinChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channel")
class JoinChannelController {

    private final JoinChannelService joinChannelService;

    public JoinChannelController(JoinChannelService joinChannelService) {
        this.joinChannelService = joinChannelService;
    }

    @PostMapping("/join")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<ApiResponse<?>> joinChannel(@RequestBody ChannelDTO channelDTO) {

        try {
            joinChannelService.joinChannel(channelDTO.uid(), channelDTO.cid());
            return new ResponseEntity<>(ApiResponse.success("加入成功"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ApiResponse.error(400, "加入失败"), HttpStatus.BAD_REQUEST);
        }

    }

}
