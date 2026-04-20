package com.parkmate.domain.exception;

public class EventFullException extends DomainException {
    public EventFullException() { super("Event is full — no spots remaining"); }
}
