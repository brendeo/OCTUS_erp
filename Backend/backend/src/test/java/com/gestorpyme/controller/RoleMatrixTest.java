package com.gestorpyme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestorpyme.support.TestAuthHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleMatrixTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void estoque_canAccessProducts() throws Exception {
        String token = TestAuthHelper.login(mockMvc, objectMapper, "estoque@gestorpyme.local", "estoque123");
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void contabil_forbiddenOnProducts() throws Exception {
        String token = TestAuthHelper.login(mockMvc, objectMapper, "contabil@gestorpyme.local", "contabil123");
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void contabil_canAccessPayables() throws Exception {
        String token = TestAuthHelper.login(mockMvc, objectMapper, "contabil@gestorpyme.local", "contabil123");
        mockMvc.perform(get("/api/payables")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
