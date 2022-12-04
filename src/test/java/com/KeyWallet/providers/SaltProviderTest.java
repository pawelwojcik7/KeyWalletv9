package com.KeyWallet.providers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaltProviderTest {

    private final SaltProvider saltProvider = new SaltProvider();

    @Test
    void generateSalt() {
       String result = saltProvider.generateSalt();
       assertNotNull(result);
       assertEquals(36, result.length());
       assertTrue(result.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
}