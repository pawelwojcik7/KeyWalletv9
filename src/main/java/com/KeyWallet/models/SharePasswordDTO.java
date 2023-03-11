package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SharePasswordDTO {

    private String login;
    private Long passwordId;
    private String masterPassword;
    private String who;

}
