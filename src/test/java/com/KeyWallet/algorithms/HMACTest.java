package com.KeyWallet.algorithms;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class HMACTest {

    private HMAC hmac;

    @BeforeMethod
    public void setUp() {
        hmac = new HMAC();
    }

    @DataProvider(name = "testCalculateHMACDataProvider")
    public static Object[][] parametersToCalculateHMACTest() {
        return new Object[][]{
                {"textToCalculateHMAC123", "KeyToHmAc", "kV/3JgEdBqFz2730n5Hsr+qcIMBM8pxXL5rJElbuvoY1BKhhf+k7oOOnJsa4OWP+RsK5LbIu9E9GPh2HFAA/3A=="},
                {"SecondTextToCalculateHMAC345", "keyToHMAC2", "cNwqEtnp4KLEaRonNUHhWKUF+wyatq6dctBygIWimYcKLyf4nyn6+sz3JOcPUO6QgTE5xWlR4QkApvPMV7j1oQ=="}};
    }

    @Test(dataProvider = "testCalculateHMACDataProvider")
    public void testCalculateHMAC(String text, String key, String expected) {

        String result = hmac.calculateHMAC(text, key);
        assertEquals(result, expected);
    }

}