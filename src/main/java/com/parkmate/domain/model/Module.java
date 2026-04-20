package com.parkmate.domain.model;

public enum Module {
    SPORTS, LUNCH, BUILD, GAMING, MOVIE;

    public static Module from(String s) {
        try { return valueOf(s.toUpperCase()); }
        catch (Exception e) { throw new IllegalArgumentException("Invalid module: " + s); }
    }
}
