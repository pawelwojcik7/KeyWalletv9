package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private String login;
    private String password;
    private Boolean keepPasswordAsHash;

}
