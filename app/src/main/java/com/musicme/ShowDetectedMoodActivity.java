package com.musicme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ShowDetectedMoodActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detected_mood_layout);

        Button save_mood= findViewById(R.id.button_Save_Mood);
        Button detect_mood= findViewById(R.id.button_remeasure_mood);
        //Button detect_mood= findViewById(R.id.detect_my_mood_button);


        // <global menu buttons
        Button bMood = findViewById(R.id.buttonMood);
        Button bMusic = findViewById(R.id.buttonMusic);
        Button bSettings = findViewById(R.id.buttonSettings);

        bMood.setEnabled(false);
        bMusic.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        // global menu buttons>

        save_mood.setOnClickListener(this);
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
//            case R.id.button_Save_Mood:
//                intent = new Intent(this, .class);
//                startActivity(intent);
//                finish();
//                break;
            case R.id.button_remeasure_mood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }
}
