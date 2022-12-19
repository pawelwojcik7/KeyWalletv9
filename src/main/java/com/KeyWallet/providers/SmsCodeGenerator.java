package com.KeyWallet.providers;

import org.springframework.stereotype.Component;

@Component
public class SmsCodeGenerator {

    public Integer generateCode(){

        return (int) (Math.random() * 8999) + 1000;
    }
}
