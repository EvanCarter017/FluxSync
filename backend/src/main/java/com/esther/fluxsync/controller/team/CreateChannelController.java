package com.esther.fluxsync.controller.team;

import com.esther.fluxsync.model.dto.ApiResponse;
import com.esther.fluxsync.model.dto.ChannelDTO;
import com.esther.fluxsync.service.channel.CreateChannelService;
import com.esther.fluxsync.utils.SmartIdUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/channel")
class CreateChannelController {

    private final CreateChannelService createChannelService;

    public CreateChannelController(CreateChannelService createChannelService) {
        this.createChannelService = createChannelService;
    }

    @PostMapping("/create")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<ApiResponse<?>> createChannel(@RequestBody ChannelDTO channelDTO) {

        String cid = SmartIdUtil.generate();

        try {
            createChannelService.createChannel(channelDTO.uid(), cid, channelDTO.name(), channelDTO.description());
            return new ResponseEntity<>(ApiResponse.success("创建成功"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ApiResponse.error(400, "创建失败"), HttpStatus.BAD_REQUEST);
        }

    }

}
