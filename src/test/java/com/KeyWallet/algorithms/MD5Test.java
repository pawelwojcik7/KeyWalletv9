package com.KeyWallet.algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MD5Test {

    MD5 md5 = new MD5();

    @Test
    public void calculateMD5() {
        byte[] springBootTests = md5.calculateMD5("SpringBootTest");
        byte[] expectedValue = new byte[]{44, -77, 36, 102, -48, 84, 91, -4, -70, -32, -91, 113, -68, -28, -78, -76};
        assertArrayEquals(expectedValue, springBootTests);
    }
}