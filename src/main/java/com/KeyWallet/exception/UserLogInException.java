package com.KeyWallet.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserLogInException extends RuntimeException{

    public UserLogInException(String message) {
        super(message);
    }

}
