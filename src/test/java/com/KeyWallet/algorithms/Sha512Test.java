package com.KeyWallet.algorithms;

import com.KeyWallet.models.Pair;
import com.KeyWallet.providers.SaltProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Sha512Test {

    private Sha512 sha512;

    @BeforeMethod
    public void setUp() {
        sha512 = new Sha512(new SaltProvider());
    }

    @DataProvider(name = "testEncodeHashValueWithGivenSaltDataProvider")
    public static Object[][] parametersEncodeHashValueWithGivenSaltTest() {
        return new Object[][]{
                {"textToTest", "Salt", "34639b8dc7146cdd533497d55bb33447fb5bcdd6cd1e702f1be7acc808570ff0f5c201ca2f4" +
                        "f0eeb70ea19315ebd57faa75d8a672a7010d9a84910bc41ad42fd"},
                {"SecondText", "", "11f05da91655017ece012aac6030ce6d906b128454619b6bafbf1d1b5a821ba7acd007ca827c744" +
                        "6eb575d5ae0c3e864fe500b679239f9df1f520f3cbef5bd84"}};
    }

    @DataProvider(name = "testEncodeHashValueWithoutSaltDataProvider")
    public static Object[][] parametersToEncodeHashValueWithoutSaltTest() {
        return new Object[][]{
                {"textToTest"}, {"SecondText"}};
    }


    @Test(dataProvider = "testEncodeHashValueWithGivenSaltDataProvider")
    public void testEncodeHashValueWithGivenSalt(String text, String salt, String expected) {

        Pair<String, String> resultPair = sha512.encodeHashValue(text, salt);

        assertEquals(resultPair.getLeft(), expected);
        assertEquals(resultPair.getRight(), salt);
    }

    @Test(dataProvider = "testEncodeHashValueWithoutSaltDataProvider")
    public void testEncodeHashValueWithoutSalt(String text) {

        Pair<String, String> resultPair = sha512.encodeHashValue(text, null);

        assertNotNull(resultPair.getRight());
        assertNotNull(resultPair.getLeft());
    }

}