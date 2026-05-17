package com.brababiba.billing.exception;

import com.brababiba.billing.common.ErrorMessages;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super(ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
    }
}
