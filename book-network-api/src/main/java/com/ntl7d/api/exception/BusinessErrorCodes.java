package com.ntl7d.api.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"), INCORRECT_CURRENT_PASSWORD(300,
            HttpStatus.BAD_REQUEST, "Incorrect current password"), NEW_PASSWORD_NOT_MATCHED(301,
                    HttpStatus.BAD_REQUEST,
                    "The new password and confirm password do not match"), ACCOUNT_LOCKED(302,
                            HttpStatus.FORBIDDEN, "Account is locked"), ACCOUNT_DISABLE(303,
                                    HttpStatus.FORBIDDEN, "Account is disabled"), BAD_CREDENTIALS(
                                            304, HttpStatus.FORBIDDEN, "Invalid credentials"),;

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }

}
