package com.KeyWallet.providers;

import org.springframework.stereotype.Component;

@Component
public class SmsCodeProvider {
    public int generateSmsCode(){
        return (int) (Math.random()*8999) + 1000;
    }
}
