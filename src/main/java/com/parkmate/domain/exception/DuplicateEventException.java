package com.parkmate.domain.exception;

/** Thrown when user tries to create a 2nd event for same activity */
public class DuplicateEventException extends DomainException {
    private final String activity;
    private final Long existingEventId;

    public DuplicateEventException(String activity, Long existingEventId) {
        super("You already have an active " + activity + " event");
        this.activity = activity;
        this.existingEventId = existingEventId;
    }

    public String getActivity() { return activity; }
    public Long getExistingEventId() { return existingEventId; }
}
