package com.musicme;

public class CurrentMoodState {
    public static int get() {
        return mood;
    }

    public static void set(int new_mood) {
        mood = new_mood;
    }

    private static int mood;
}
