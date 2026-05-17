package com.brababiba.billing.exception;

import com.brababiba.billing.common.ErrorMessages;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super(ErrorMessages.EMAIL_EXISTS + email);
    }
}
