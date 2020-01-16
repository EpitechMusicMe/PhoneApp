package com.musicme;

public enum Mood {
    HAPPY ("Happy"),
    SAD ("Sad"),
    NONE ("None");
    // TODO: add the remaining moods

    private final String label;

    Mood(String label) {
        this.label = label;
    }

    public String label() { return label; }
}
