package com.esther.fluxsync.model.dto;

public record ChannelDTO(

        int uid,
        String name,
        String cid,
        String description

) {

    public static ChannelDTO of(int uid, String name, String cid, String description) {
        return new ChannelDTO(uid, name, cid, description);
    }

    public static ChannelDTO empty() {
        return new ChannelDTO(0, null, null, null);
    }

}
