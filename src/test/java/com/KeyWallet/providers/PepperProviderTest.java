package com.KeyWallet.providers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.testng.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PepperProvider.class)
@TestPropertySource("classpath:application.properties")
public class PepperProviderTest {

    @Autowired
    private PepperProvider pepperProvider;

    private static final String pepper = "1029-3847-5665-7483-9201-aAzZ-qWEr";

    @Test
    public void testGetPepper() {

        String result = pepperProvider.getPepper();

        assertEquals(result, pepper);
    }

}