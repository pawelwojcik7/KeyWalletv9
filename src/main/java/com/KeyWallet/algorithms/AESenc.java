package com.KeyWallet.algorithms;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AESenc {

    private final MD5 md5;
    private static final String ALGO = "AES";

    @SneakyThrows
    public String encrypt(String data, Key key) {

        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(encVal);
    }

    @SneakyThrows
    public String decrypt(String encryptedData, Key key) {

        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decodedValue);

        return new String(decValue);
    }

    public Key generateKey(String password) {

        return new SecretKeySpec(md5.calculateMD5(password), ALGO);
    }
}
