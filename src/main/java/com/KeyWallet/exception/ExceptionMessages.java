package com.KeyWallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
public enum ExceptionMessages {

    USER_DOES_NOT_EXIST("User does not exists!"),
    USER_ALREADY_EXIST("User already exist!");

    @Getter
    public final String code;

//
//    public static ExceptionMessages getByValue(String value){
//        return Arrays.stream(values())
//                .filter(exceptionMessage -> exceptionMessage.getCode().equals(value))
//                .findFirst()
//                .orElseThrow(IllegalArgumentException::new);
//    }

    }
