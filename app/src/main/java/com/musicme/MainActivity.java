package com.musicme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton bBluetooth = findViewById(R.id.button_bluetooth);

        bBluetooth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (view.getId()) {
                    case R.id.button_bluetooth:
                        intent = new Intent(MainActivity.this,BluetoothActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

            }
        });

    }
}
