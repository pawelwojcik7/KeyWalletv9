package com.KeyWallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionMessages {

    USER_DOES_NOT_EXIST("User does not exists!"),
    USER_ALREADY_EXIST("User already exist!"),
    WRONG_PASSWORD("Wrong password!"),
    PASSWORD_DOES_NOT_EXIST("Password does not exists!"),
    INVALID_SMS_CODE("Invalid SMS code!"),

    PERMANENT_BLOCK_IP_ADDRESS("Ip Address is blocked permanent"),

    TEMP_BLOCK_IP_ADDRESS("Ip Address is blocked temporarily until: "),

    NEW_PERMANENT_BLOCK_IP_ADDRESS("The ip address has been blocked permanent."),

    NEW_TEMP_BLOCK_IP_ADDRESS("The ip address has been blocked to: "),

    WRONG_PASSWORD_FORMAT("Wrong password format");


    @Getter
    public final String code;

}
