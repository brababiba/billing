package com.brababiba.billing.dto;

import com.brababiba.billing.common.ErrorMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequest {
    @NotBlank(message = ErrorMessages.NAME_REQUIRED)
    private String name;
}
