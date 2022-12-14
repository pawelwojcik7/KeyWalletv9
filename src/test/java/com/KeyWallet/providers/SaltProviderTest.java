package com.KeyWallet.providers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SaltProviderTest {

    private SaltProvider saltProvider;

    @BeforeMethod
    public void setUp() {
        saltProvider = new SaltProvider();
    }

    @Test
    public void testGenerateSalt() {

        String result = saltProvider.generateSalt();

        assertNotNull(result);
        assertEquals(36, result.length());
        assertTrue(result.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
}