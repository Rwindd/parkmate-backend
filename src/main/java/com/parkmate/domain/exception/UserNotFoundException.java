package com.parkmate.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String deviceId) {
        super("No user found with deviceId: " + deviceId);
    }
    public UserNotFoundException(Long id) {
        super("No user found with id: " + id);
    }
}
