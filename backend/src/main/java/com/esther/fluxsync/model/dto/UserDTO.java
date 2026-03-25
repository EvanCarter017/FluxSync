package com.esther.fluxsync.model.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String password;
    private Boolean exp;
    /** Totp 动态口令 */
    private int code;

}
