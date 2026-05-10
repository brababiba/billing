package com.brababiba.billing.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void deleteAccountShouldReturn204() throws Exception {
        String createBody = """
                {
                    "name": "toDelete"
                }
                """;
        String response = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/accounts/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/accounts/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAccountShouldChangeName() throws Exception {
        String createBody = """
                {
                    "name": "OldName"
                }
                """;

        String createResponse = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andReturn().getResponse().getContentAsString();

        String id = JsonPath.read(createResponse, "$.id");

        String updateBody = """
                {
                    "name": "NewName"
                }
                """;

        mockMvc.perform(put("/api/accounts/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));

        mockMvc.perform(get("/api/accounts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    void getAllShouldReturnAccountsList() throws Exception {

        String body1 = """
                {
                    "name": "User1"
                }
                """;

        String body2 = """
                {
                    "name": "User2"
                }
                """;

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body1));

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body2));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].createdAt").exists());
    }
}
