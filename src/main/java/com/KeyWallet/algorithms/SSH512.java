package com.KeyWallet.algorithms;

import com.KeyWallet.interfaces.EncryptAlgorithm;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;

@Component
public class SSH512 implements EncryptAlgorithm {

    @SneakyThrows
    @Override
    public String encrypt(String text, String pepper, String salt) {
        text = text + salt + pepper;
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest(text.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

}
