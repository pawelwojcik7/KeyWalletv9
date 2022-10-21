package com.KeyWallet.models;

import lombok.Data;

@Data
public class UserDTO {
    private String login;
    private String password;
    private Boolean keepPasswordAsHash;
}
