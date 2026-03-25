package com.esther.fluxsync.model.dto.team;

import java.util.List;

public record ChannelMessageDTO(

        String payload,
        Long seq

) {

    public static ChannelMessageDTO of(String payload, Long seq) {
        return new ChannelMessageDTO(payload, seq);
    }

    public static ChannelMessageDTO empty() {
        return new ChannelMessageDTO("", -1L);
    }

}
