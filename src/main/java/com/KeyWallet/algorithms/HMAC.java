package com.KeyWallet.algorithms;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA512;

@Component
public class HMAC {

    @SneakyThrows
    public String encrypt(String text, String pepper, String salt) {

        final byte[] byteKey = pepper.getBytes(StandardCharsets.UTF_8);
        Mac sha512Hmac = Mac.getInstance(HMAC_SHA512);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        sha512Hmac.init(keySpec);
        byte[] macData = sha512Hmac.doFinal((text+salt).getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(macData);
    }

}
