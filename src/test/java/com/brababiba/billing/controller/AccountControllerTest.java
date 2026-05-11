package com.brababiba.billing.controller;

import com.brababiba.billing.common.ErrorMessages;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
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
        String body = createAccountBody("Igor");
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

        String body = createAccountBody("");

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name").value(ErrorMessages.NAME_REQUIRED));
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

        String createBody = createAccountBody("toDelete");

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

        String id = createAccountAndReturnId("OldName");

        String updateBody = createAccountBody("NewName");

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

        createAccountAndReturnId("User1");
        createAccountAndReturnId("User2");

        mockMvc.perform(get("/api/accounts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getAllShouldFilterAccountsByName() throws Exception {

        createAccountAndReturnId("Alpha");
        createAccountAndReturnId("Beta");

        mockMvc.perform(get("/api/accounts")
                        .param("name", "alp")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Alpha"));
    }

    @Test
    void getByIdShouldReturnAccountWhenExists() throws Exception {

        String id = createAccountAndReturnId("FindMe");

        mockMvc.perform(get("/api/accounts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("FindMe"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void updateShouldReturn400WhenNameIsBlank() throws Exception {

        String id = createAccountAndReturnId("InitialName");

        String updateBody = createAccountBody("");

        mockMvc.perform(put("/api/accounts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void updateShouldReturn404WhenAccountDoesNotExist() throws Exception {

        String randomId = "11111111-1111-1111-1111-111111111111";

        String updateBody = createAccountBody("NewName");

        mockMvc.perform(put("/api/accounts/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Account not found: " + randomId));
    }

    @Test
    void deleteShouldReturn404WhenAccountDoesNotExist() throws Exception {

        String randomId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(delete("/api/accounts/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Account not found: " + randomId));
    }

    @Test
    void getByIdShouldReturn400WhenUuidIsInvalid() throws Exception {
        mockMvc.perform(get("/api/accounts/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenNameIsMissing() throws Exception {

        String body = """
                {
                }
                """;

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void updateShouldReturn400WhenNameIsMissing() throws Exception {

        String id = createAccountAndReturnId("InitialName");

        String updateBody = """
                {
                }
                """;

        mockMvc.perform(put("/api/accounts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void createShouldReturn400WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MALFORMED_JSON_REQUEST));
    }

    @Test
    void createShouldReturn400WhenJsonIsInvalid() throws Exception {

        String invalidJson = """
                {
                    "name":
                }
                """;

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MALFORMED_JSON_REQUEST));
    }

    @Test
    void updateShouldReturn400WhenUuidIsInvalid() throws Exception {

        String body = createAccountBody("UpdatedName");

        mockMvc.perform(put("/api/accounts/invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.INVALID_REQUEST_PARAMETER));
    }

    @Test
    void deleteShouldReturn400WhenUuidIsInvalid() throws Exception {

        mockMvc.perform(delete("/api/accounts/invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.INVALID_REQUEST_PARAMETER));
    }

    @NonNull
    private static String createAccountBody(String name) {
        return """
                {
                    "name": "%s"
                }
                """.formatted(name);
    }

    private String createAccountAndReturnId(String name) throws Exception {

        String createResponse = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAccountBody(name)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(createResponse, "$.id");
    }
}
