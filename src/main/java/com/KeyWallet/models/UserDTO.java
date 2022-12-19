package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDTO {

    private String login;
    private String password;
    private Boolean keepPasswordAsHash;

}
