package com.parkmate.domain.exception;

/** Base for all domain rule violations */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) { super(message); }
}
