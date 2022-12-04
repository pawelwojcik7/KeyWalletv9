package com.KeyWallet.algorithms;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MD5Test {

    private MD5 md5;

    @BeforeMethod
    public void setUp() {
        md5 = new MD5();
    }

    @DataProvider(name = "testCalculateMD5DataProvider")
    public static Object[][] parametersToGenerateKeyTest() {
        return new Object[][]{
                {"SpringBootTest", new byte[]{44, -77, 36, 102, -48, 84, 91, -4, -70, -32, -91, 113, -68, -28, -78, -76}},
                {"8h2r8h3finADNAUIYDB234i2", new byte[]{-93, 89, 68, -26, -73, 51, 89, 49, -60, 73, -12, -94, 100, 116, 52, -81}}};
    }


    @Test(dataProvider = "testCalculateMD5DataProvider")
    public void testCalculateMD5(String text, byte[] expected) {
        byte[] result = md5.calculateMD5(text);
        assertArrayEquals(result, expected);
    }
}