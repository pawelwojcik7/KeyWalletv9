package com.KeyWallet.algorithms;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class MD5 {

    @SneakyThrows
    public byte[] calculateMD5(String text) {

        MessageDigest md = MessageDigest.getInstance("MD5");

        return md.digest(text.getBytes());

    }
}
