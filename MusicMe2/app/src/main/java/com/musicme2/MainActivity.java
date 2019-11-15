package com.musicme2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "5f2f2cd771174cfcb2e3a8433c47935c";
    public static final String REDIRECT_URI = "musicme://auth/callback/";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent;
//        intent = new Intent(MainActivity.this, SpotifyAuthActivity.class);
//        startActivity(intent);
//        finish();

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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
//
//        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
//            mAccessToken = response.getAccessToken();
//        }
//    }

    public void authenticateWithSpotify(View view) {
        // an auth window does not open
        // if the user is already signed in to the spotify app on their phone
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }
}
