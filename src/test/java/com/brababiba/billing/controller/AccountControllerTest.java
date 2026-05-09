package com.brababiba.billing.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAccountShouldReturnOk() throws Exception {
        String body = """
                {
                    "name": "Igor"
                }
                """;
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Igor"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createAccountShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        String body = """
                {
                    "name": ""
                }
                """;
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Name is required"));
    }

    @Test
    void getByIdShouldReturn404WhenAccountDoesNotExist() throws Exception {
        String randomId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(get("/api/accounts/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found: " + randomId));
    }
}
