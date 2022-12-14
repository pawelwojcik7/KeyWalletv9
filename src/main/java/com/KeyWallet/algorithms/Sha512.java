package com.KeyWallet.algorithms;

import com.KeyWallet.models.Pair;
import com.KeyWallet.providers.SaltProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;

@Component
@RequiredArgsConstructor
public class Sha512 {

    private final SaltProvider saltProvider;

    public Pair<String, String> encodeHashValue(String password, String salt) {

        if (salt == null) {
            salt = saltProvider.generateSalt();
        }
        return new Pair<>(calculateSHA512(salt + password), salt);
    }

    @SneakyThrows
    private String calculateSHA512(String text) {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigest = md.digest(text.getBytes());
        BigInteger bigInteger = new BigInteger(1, messageDigest);
        String hashtext = bigInteger.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }

}
