package com.musicme2;

import android.content.Intent;
import android.os.Bundle;
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

    Button detect_mood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_mood_layout);

        mood_happy = (Button) findViewById(R.id.happy_button);
        mood_excited = (Button) findViewById(R.id.excited_button);
        mood_angry = (Button) findViewById(R.id.angry_button);
        mood_energetic = (Button) findViewById(R.id.energetic_button);
        mood_neutral = (Button) findViewById(R.id.neutral_button);
        mood_tired = (Button) findViewById(R.id.tired_button);
        mood_nervous = (Button) findViewById(R.id.nervous_button);
        mood_bored = (Button) findViewById(R.id.bored_button);
        detect_mood= (Button) findViewById(R.id.detect_my_mood_button);

        // <global menu buttons
        Button bMood = findViewById(R.id.buttonMood);
        Button bMusic = findViewById(R.id.buttonMusic);
        Button bSettings = findViewById(R.id.buttonSettings);

        bMood.setEnabled(false);
        bMusic.setOnClickListener(this);
        bSettings.setOnClickListener(this);


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
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

}