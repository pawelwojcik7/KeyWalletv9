package com.KeyWallet.providers;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SaltProvider {

    public String generateSalt() {
        return UUID.randomUUID().toString();
    }

}
