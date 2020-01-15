package com.musicme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: proper window management instead of onClick events to move between windows/activities
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        Intent intent = new Intent(MainActivity.this, RemotePlayerActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buttonMood:
                intent = new Intent(this, EnterMoodActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonMusic:
                intent = new Intent(this, RemotePlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonSettings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
