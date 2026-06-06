package com.gestorpyme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestorpyme.domain.enums.MovementType;
import com.gestorpyme.dto.request.CreateMovementRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mock")
class ProductControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listProducts_returnsSeedData() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(8))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(8)));
    }

    @Test
    void createMovement_entrada_increasesStock() throws Exception {
        CreateMovementRequest request = new CreateMovementRequest(
                MovementType.ENTRADA, 5, LocalDate.now(), "Teste entrada");
        mockMvc.perform(post("/api/products/1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                .andExpect(jsonPath("$.createdBy").value("admin"));
    }

    @Test
    void createMovement_saida_insufficientStock_returns400() throws Exception {
        CreateMovementRequest request = new CreateMovementRequest(
                MovementType.SAIDA, 9999, LocalDate.now(), "Teste saída inválida");
        mockMvc.perform(post("/api/products/1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"));
    }
}
