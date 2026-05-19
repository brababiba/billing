package com.brababiba.billing.controller;

import com.brababiba.billing.AbstractIntegrationTest;
import com.brababiba.billing.common.ErrorMessages;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkspaceControllerTest extends AbstractIntegrationTest {

    private String token;

    @BeforeEach
    void setup() throws Exception {

        String email = "login-user-" + System.currentTimeMillis() + "@test.com";
        String password = "234567";

        registerUser(email, password);

        token = loginAndGetToken(email, password);
    }

    @Test
    void createWorkspaceShouldReturnOk() throws Exception {
        String body = createWorkspaceBody("Igor");
        mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Igor"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createWorkspaceShouldReturnBadRequestWhenNameIsBlank() throws Exception {

        String body = createWorkspaceBody("");

        mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name").value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void getByIdShouldReturn404WhenWorkspaceDoesNotExist() throws Exception {
        String randomId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(get("/api/workspaces/" + randomId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Workspace not found: " + randomId));
    }

    @Test
    void deleteWorkspaceShouldReturn204() throws Exception {

        String createBody = createWorkspaceBody("toDelete");

        String response = mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody)
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/workspaces/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/workspaces/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateWorkspacesShouldChangeName() throws Exception {

        String email = "workspace-" + System.currentTimeMillis() + "@test.com";
        String password = "123456";

        registerUser(email, password);
        String token = loginAndGetToken(email, password);
        String workspaceId = getFirstMyWorkspaceId(token);

        String updateBody = createWorkspaceBody("NewName");

        mockMvc.perform(put("/api/workspaces/" + workspaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));

        mockMvc.perform(get("/api/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    void getAllShouldReturnWorkspacesList() throws Exception {

        createWorkspaceAndReturnId("User1");
        createWorkspaceAndReturnId("User2");

        mockMvc.perform(get("/api/workspaces")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].name").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void getAllShouldFilterWorkspacesByName() throws Exception {

        createWorkspaceAndReturnId("Alpha");
        createWorkspaceAndReturnId("Beta");

        mockMvc.perform(get("/api/workspaces")
                        .param("name", "alp")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Alpha"));
    }

    @Test
    void getByIdShouldReturnWorkspaceWhenExists() throws Exception {

        String email = "workspace-" + System.currentTimeMillis() + "@test.com";
        String password = "123456";

        registerUser(email, password);
        String token = loginAndGetToken(email, password);
        String workspaceId = getFirstMyWorkspaceId(token);

        mockMvc.perform(get("/api/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workspaceId))
                .andExpect(jsonPath("$.name").value(expectedDefaultWorkspaceName(email)))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void updateShouldReturn400WhenNameIsBlank() throws Exception {

        String id = createWorkspaceAndReturnId("InitialName");

        String updateBody = createWorkspaceBody("");

        mockMvc.perform(put("/api/workspaces/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void updateShouldReturn404WhenWorkspaceDoesNotExist() throws Exception {

        String randomId = "11111111-1111-1111-1111-111111111111";

        String updateBody = createWorkspaceBody("NewName");

        mockMvc.perform(put("/api/workspaces/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Workspace not found: " + randomId));
    }

    @Test
    void deleteShouldReturn404WhenWorkspaceDoesNotExist() throws Exception {

        String randomId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(delete("/api/workspaces/" + randomId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Workspace not found: " + randomId));
    }

    @Test
    void getByIdShouldReturn400WhenUuidIsInvalid() throws Exception {
        mockMvc.perform(get("/api/workspaces/invalid-uuid")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenNameIsMissing() throws Exception {

        String body = """
                {
                }
                """;

        mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void updateShouldReturn400WhenNameIsMissing() throws Exception {

        String id = createWorkspaceAndReturnId("InitialName");

        String updateBody = """
                {
                }
                """;

        mockMvc.perform(put("/api/workspaces/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.VALIDATION_FAILED))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .value(ErrorMessages.NAME_REQUIRED));
    }

    @Test
    void createShouldReturn400WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .header("Authorization", "Bearer " + token))
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

        mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.MALFORMED_JSON_REQUEST));
    }

    @Test
    void updateShouldReturn400WhenUuidIsInvalid() throws Exception {

        String body = createWorkspaceBody("UpdatedName");

        mockMvc.perform(put("/api/workspaces/invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.INVALID_REQUEST_PARAMETER));
    }

    @Test
    void deleteShouldReturn400WhenUuidIsInvalid() throws Exception {

        mockMvc.perform(delete("/api/workspaces/invalid-uuid")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(ErrorMessages.INVALID_REQUEST_PARAMETER));
    }

    @Test
    void getMyWorkspaceShouldReturnUserWorkspaces() throws Exception {

        String email = "workspace-user-" + System.currentTimeMillis() + "@test.com";
        String password = "123456";

        registerUser(email, password);

        String token = loginAndGetToken(email, password);

        mockMvc.perform(get("/api/workspaces/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("OWNER"));
    }

    @NonNull
    private static String createWorkspaceBody(String name) {
        return """
                {
                    "name": "%s"
                }
                """.formatted(name);
    }

    private String createWorkspaceAndReturnId(String name) throws Exception {

        String createResponse = mockMvc.perform(post("/api/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createWorkspaceBody(name))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(createResponse, "$.id");
    }

    private String getFirstMyWorkspaceId(String token) throws Exception {

        String response = mockMvc.perform(get("/api/workspaces/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$[0].id");
    }

    private String expectedDefaultWorkspaceName(String email) {
        return email.split("@")[0] + "'s workspace";
    }
}
