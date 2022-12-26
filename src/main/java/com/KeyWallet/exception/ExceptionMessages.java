package com.KeyWallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionMessages {

    USER_DOES_NOT_EXIST("User does not exists!"),
    USER_ALREADY_EXIST("User already exist!"),
    WRONG_PASSWORD("Wrong password!"),
    PASSWORD_DOES_NOT_EXIST("Password does not exists!"),
    INVALID_SMS_CODE("Invalid SMS code!");



    @Getter
    public final String code;

}
