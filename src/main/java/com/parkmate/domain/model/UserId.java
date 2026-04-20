package com.parkmate.domain.model;

/** Value Object — strongly typed ID prevents primitive obsession */
public record UserId(Long value) {
    public static UserId of(Long value) { return new UserId(value); }
    public String toString() { return value.toString(); }
}
