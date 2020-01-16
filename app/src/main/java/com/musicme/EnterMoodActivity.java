package com.musicme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EnterMoodActivity extends AppCompatActivity implements View.OnClickListener {

    Button mood_happy;
    Button mood_excited;
    Button mood_angry;
    Button mood_energetic;
    Button mood_neutral;
    Button mood_tired;
    Button mood_nervous;
    Button mood_bored;

    static String mood_button_tag = "MOODBUTTON";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mood);

        mood_happy = (Button) findViewById(R.id.happy_button);
        mood_excited = (Button) findViewById(R.id.excited_button);
        mood_angry = (Button) findViewById(R.id.angry_button);
        mood_energetic = (Button) findViewById(R.id.energetic_button);
        mood_neutral = (Button) findViewById(R.id.neutral_button);
        mood_tired = (Button) findViewById(R.id.tired_button);
        mood_nervous = (Button) findViewById(R.id.nervous_button);
        mood_bored = (Button) findViewById(R.id.bored_button);
        Button detect_mood = findViewById(R.id.detect_my_mood_button);
//
//        // <global menu buttons
//        Button bMood = findViewById(R.id.buttonMood);
//        Button bMusic = findViewById(R.id.buttonMusic);
//        Button bSettings = findViewById(R.id.buttonSettings);
//
//        bMood.setEnabled(false);
//        bMusic.setOnClickListener(this);
//        bSettings.setOnClickListener(this);
//        // global menu buttons>
//
        detect_mood.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buttonMood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonMusic:
                intent = new Intent(this, MusicPlayerActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
//            case R.id.detect_my_mood_button:
//                intent = new Intent(this, ShowDetectedMoodActivity.class);
//                startActivity(intent);
//                finish();
//                break;
        }
    }

    public void onClickMoodButton(View view) {
        switch (view.getId()) {
            case R.id.happy_button:
                Log.d("MOOD", "happy");
                break;
            case R.id.sad_button:
                Log.d("MOOD", "sad");
                break;
            default:
                Log.w(mood_button_tag, "Mood button press not handled.");
        }
    }

}