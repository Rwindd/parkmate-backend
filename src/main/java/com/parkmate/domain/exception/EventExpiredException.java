package com.parkmate.domain.exception;

public class EventExpiredException extends DomainException {
    public EventExpiredException() {
        super("This event has already started — joins are closed");
    }
}
