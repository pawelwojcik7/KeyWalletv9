package com.KeyWallet.providers;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SaltProvider {

    public String generateSalt(){
        return UUID.randomUUID().toString();
    }


}
