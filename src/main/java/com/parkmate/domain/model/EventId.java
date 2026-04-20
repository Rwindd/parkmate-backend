package com.parkmate.domain.model;

public record EventId(Long value) {
    public static EventId of(Long value) { return new EventId(value); }
    public String toString() { return value.toString(); }
}
