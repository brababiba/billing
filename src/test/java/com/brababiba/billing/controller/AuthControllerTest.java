package com.brababiba.billing.controller;

import com.brababiba.billing.AbstractIntegrationTest;
import com.brababiba.billing.model.AccountMemberId;
import com.brababiba.billing.model.User;
import com.brababiba.billing.model.UserRole;
import com.brababiba.billing.model.UserRoleId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest extends AbstractIntegrationTest {

    @Test
    void registerShouldCreateUserWithDefaultRole() throws Exception {

        String email = "test-user-" + System.currentTimeMillis() + "@test.com";

        registerUser(email, "123456");

        var user = userRepository.findByEmail(email)
                .orElseThrow();

        assertEquals(email, user.getEmail());
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().stream()
                .anyMatch(role -> "USER".equals(role.getId().getRole())));

        var accounts = accountRepository.findAll();

        assertFalse(accounts.isEmpty());

        var account = accounts.stream()
                .filter(a -> a.getName().contains(email.split("@")[0]))
                .findFirst()
                .orElseThrow();

        var memberId = new AccountMemberId();
        memberId.setAccountId(account.getId());
        memberId.setUserId(user.getId());

        var accountMember = accountMemberRepository.findById(memberId)
                .orElseThrow();

        assertEquals("OWNER", accountMember.getRole());
    }

    @Test
    void loginShouldReturnJwtToken() throws Exception {

        String email = "login-user-" + System.currentTimeMillis() + "@test.com";
        String password = "234567";

        registerUser(email, password);

        String token = loginAndGetToken(email, password);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void getCurrentUserWithoutTokenShouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUserWithValidTokenShouldReturnOk() throws Exception {

        String email = "secured-user-" + System.currentTimeMillis() + "@test.com";
        String password = "123456";

        registerUser(email, password);

        String token = loginAndGetToken(email, password);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void adminEndpointWithUserRoleShouldReturnForbidden() throws Exception {

        String email = "user-role-" + System.currentTimeMillis() + "@test.com";
        String password = "123456";

        registerUser(email, password);

        String token = loginAndGetToken(email, password);

        mockMvc.perform(get("/api/auth/admin-test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointWithAdminRoleShouldReturnOk() throws Exception {

        String email = "admin-role-" + System.currentTimeMillis() + "@test.com";
        String password = "123456";

        registerUser(email, password);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        UserRoleId roleId = new UserRoleId();
        roleId.setUserId(user.getId());
        roleId.setRole("ADMIN");

        UserRole adminRole = new UserRole();
        adminRole.setId(roleId);

        userRoleRepository.save(adminRole);

        String token = loginAndGetToken(email, password);

        mockMvc.perform(get("/api/auth/admin-test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithWrongPasswordShouldReturnUnauthorized() throws Exception {

        String email = "wrong-password-" + System.currentTimeMillis() + "@test.com";

        registerUser(email, "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginRequest(email, "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithUnknownEmailShouldReturnUnauthorized() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginRequest("unknown@test.com", "123456")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerWithDuplicateEmailShouldReturnConflict() throws Exception {

        String email = "duplicate-" + System.currentTimeMillis() + "@test.com";

        registerUser(email, "123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRegisterRequest(email, "123456")))
                .andExpect(status().isConflict());
    }

    @Test
    void protectedEndpointWithInvalidTokenShouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}