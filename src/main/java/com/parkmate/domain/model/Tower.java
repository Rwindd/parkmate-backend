package com.parkmate.domain.model;

/** Value Object — enum-backed tower validation */
public enum Tower {
    CITIUS, ALTIUS, FORTIUS;

    public static Tower from(String s) {
        try { return valueOf(s.toUpperCase()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid tower: " + s); }
    }
}
