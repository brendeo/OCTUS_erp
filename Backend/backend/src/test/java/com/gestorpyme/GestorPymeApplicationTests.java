package com.gestorpyme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mock")
class GestorPymeApplicationTests {

    @Test
    void contextLoadsMockProfile() {
    }
}
