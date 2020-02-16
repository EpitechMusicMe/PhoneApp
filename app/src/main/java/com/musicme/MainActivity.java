package com.musicme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    //TextView currentMood = findViewById(R.id.current_mood_value);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: proper window management instead of onClick events to move between windows/activities
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView currentMood = findViewById(R.id.current_mood_value);
        currentMood.setText(CurrentMoodState.get().label());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClickBluetooth(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void onClickSpotify(View view) {
        Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buttonMood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonMusic:
                intent = new Intent(this, MusicPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
