package com.musicme;

public enum Mood {
    HAPPY ("Happy"),
    EXCITED("Excited"),
    ANGRY("Angry"),
    ENERGETIC("Energetic"),
    NEUTRAL("Neutral"),
    TIRED("Tired"),
    NERVOUS("Nervous"),
    BORED("Bored"),
    SAD ("Sad"),
    NONE("None");

    private final String label;

    Mood(String label) {
        this.label = label;
    }

    public String label() { return label; }
}
