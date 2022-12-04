package com.KeyWallet.algorithms;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AESencTest {

    private AESenc aeSenc;

    @BeforeMethod
    public void setUp() {

        aeSenc = new AESenc(new MD5());
    }

    @DataProvider(name = "testEncryptDataProvider")
    public static Object[][] parametersToEncryptTest() {

        return new Object[][]{
                {"password", "asd-dsa-123-321", "+T+/9Zd976Q7KDoe5EXC/w=="},
                {"password1", "123-456-789-asd", "aoLZJWt4UsalCuXPlnoRkA=="}};
    }

    @DataProvider(name = "testDecryptDataProvider")
    public static Object[][] parametersToDecryptTest() {

        return new Object[][]{
                {"password", "asd-dsa-123-321", "+T+/9Zd976Q7KDoe5EXC/w=="},
                {"password1", "123-456-789-asd", "aoLZJWt4UsalCuXPlnoRkA=="}};
    }

    @DataProvider(name = "testGenerateKeyDataProvider")
    public static Object[][] parametersToGenerateKeyTest() {

        return new Object[][]{
                {"password", new byte[]{95, 77, -52, 59, 90, -89, 101, -42, 29, -125, 39, -34, -72, -126, -49, -103}},
                {"IdKwSbHbIT09091213", new byte[]{-84, -103, 85, 29, -28, 119, -17, -43, -31, -123, 84, -115, -45, -80, 111, -49}}};
    }

    @Test(dataProvider = "testEncryptDataProvider")
    public void testEncrypt(String password, String dataToEncrypt, String expected) {

        Key key = aeSenc.generateKey(password);
        String result = aeSenc.encrypt(dataToEncrypt, key);

        assertEquals(result, expected);
    }

    @Test(dataProvider = "testDecryptDataProvider")
    public void testDecrypt(String password, String expected, String encryptString) {

        Key key = aeSenc.generateKey(password);
        String result = aeSenc.decrypt(encryptString, key);

        assertEquals(result, expected);
    }

    @Test(dataProvider = "testGenerateKeyDataProvider")
    public void testGenerateKey(String password, byte[] expected) {

        Key passwordKey = aeSenc.generateKey(password);

        assertEquals("AES", passwordKey.getAlgorithm());
        assertEquals("RAW", passwordKey.getFormat());
        assertArrayEquals(passwordKey.getEncoded(), expected);
    }
}