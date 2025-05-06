
package com.chatty.auth.exception;

public class ApiKeyAlreadyExistsException extends RuntimeException {
    public ApiKeyAlreadyExistsException(String message) {
        super(message);
    }
}
