package com.gestorpyme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestorpyme.support.TestAuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PayableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String gestorToken;

    @BeforeEach
    void setUp() throws Exception {
        gestorToken = TestAuthHelper.login(mockMvc, objectMapper, "admin@gestorpyme.local", "admin123");
    }

    @Test
    void createAndMarkPaid() throws Exception {
        String body = """
                {"descricao":"Teste conta","valor":150.00,"vencimento":"2026-12-31"}
                """;
        String location = mockMvc.perform(post("/api/payables")
                        .header("Authorization", "Bearer " + gestorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(location).get("id").asLong();

        mockMvc.perform(patch("/api/payables/" + id + "/mark-paid")
                        .header("Authorization", "Bearer " + gestorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGA"));
    }
}
