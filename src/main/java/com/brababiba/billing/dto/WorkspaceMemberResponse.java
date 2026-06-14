package com.brababiba.billing.dto;

public record WorkspaceMemberResponse(
        String userId,
        String email,
        String role
) {
}
