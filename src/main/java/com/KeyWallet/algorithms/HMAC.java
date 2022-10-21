package com.KeyWallet.algorithms;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HMAC {

    private static final String HMAC_SHA512 = "HmacSHA512";

    @SneakyThrows
    public String calculateHMAC(String text, String key) {

        final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
        Mac sha512Hmac = Mac.getInstance(HMAC_SHA512);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        sha512Hmac.init(keySpec);
        byte[] macData = sha512Hmac.doFinal(text.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(macData);
    }

}
