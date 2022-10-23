package com.KeyWallet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class CryptResponse implements Serializable {
    private String cryptPassword;
}
