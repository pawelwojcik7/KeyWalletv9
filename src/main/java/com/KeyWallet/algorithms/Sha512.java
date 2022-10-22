package com.KeyWallet.algorithms;

import com.KeyWallet.entity.UserKW;
import com.KeyWallet.models.UserDTO;
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

    public UserKW encodeHashValue(UserDTO userDTO, String salt) {

        if(salt==null){
            salt = saltProvider.generateSalt();
        }

        String password = calculateSHA512(salt + userDTO.getPassword());

        return new UserKW(null, userDTO.getLogin(), password, salt, userDTO.getKeepPasswordAsHash());
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
