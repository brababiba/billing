package com.brababiba.billing.dto.auth;

import com.brababiba.billing.common.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
    @Email(message = ErrorMessages.INVALID_EMAIL)
    private String email;

    @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
    private String password;
}
