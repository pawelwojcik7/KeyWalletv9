package com.KeyWallet.algorithms;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;

@Component
public class SSH512{

    @SneakyThrows
    public String encrypt(String text, String pepper, String salt) {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest((text+salt+pepper).getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }

}
