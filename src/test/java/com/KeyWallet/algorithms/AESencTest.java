package com.KeyWallet.algorithms;

import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AESencTest {

    private final AESenc aeSenc = new AESenc(new MD5());
    private final static String password = "password";
    private final static byte[] expectedKey =
            new byte[]{95, 77, -52, 59, 90, -89, 101, -42, 29, -125, 39, -34, -72, -126, -49, -103};
    private final static String dataToEncrypt = "asd-dsa-123-321";
    private final static String expectedEncryptString = "+T+/9Zd976Q7KDoe5EXC/w==";


    @Test
    void encrypt() {

        Key key = aeSenc.generateKey(password);
        String encryptString = aeSenc.encrypt(dataToEncrypt, key);

        assertEquals(expectedEncryptString, encryptString);
    }

    @Test
    void decrypt() {

        Key key = aeSenc.generateKey(password);
        String decryptString = aeSenc.decrypt(expectedEncryptString, key);

        assertEquals(dataToEncrypt, decryptString);
    }

    @Test
    void generateKey() {

        Key passwordKey = aeSenc.generateKey(password);

        assertEquals("AES", passwordKey.getAlgorithm());
        assertEquals("RAW", passwordKey.getFormat());
        assertArrayEquals(expectedKey, passwordKey.getEncoded());

    }
}