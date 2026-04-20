package com.parkmate.domain.exception;

public class EventNotFoundException extends DomainException {
    public EventNotFoundException(Long id) {
        super("Event not found: " + id);
    }
}
