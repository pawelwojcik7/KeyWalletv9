package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest implements Serializable {

    private String login;
    private String password;
    private Boolean keepAsHash;
}
