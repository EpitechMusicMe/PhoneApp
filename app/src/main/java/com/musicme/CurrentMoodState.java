package com.musicme;

public class CurrentMoodState {

    public static Mood get() {
        return mood;
    }

    public static void set(Mood new_mood) {
        mood = new_mood;
    }

    private static Mood mood = Mood.NONE;
}
