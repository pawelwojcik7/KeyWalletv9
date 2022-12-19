package com.KeyWallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum ExceptionMessages {

    USER_DOES_NOT_EXIST("User does not exists!"),
    USER_ALREADY_EXIST("User already exist!"),
    WRONG_PASSWORD("Wrong password!"),
    PASSWORD_DOES_NOT_EXIST("Password does not exists!"),

    TWO_FAILED_USER_LOGINS("Try again in 5 sec!"),
    THREE_FAILED_USER_LOGINS("Try again in 10 sec!"),
    FOUR_FAILED_USER_LOGINS("Try again in 2 mins!"),
    FIVE_FAILED_USER_LOGINS("Try again in 30 min!"),
    FAILED_IP_LOGIN("Try again in 30min from this IP or unblock it!"),
    BLOCK_ACCOUNT_PERM("Logging in from this ip address is blocked!");

    @Getter
    public final String code;

    public static ExceptionMessages getByValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.getCode().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
