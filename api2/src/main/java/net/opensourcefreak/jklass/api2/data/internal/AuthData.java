package net.opensourcefreak.jklass.api2.data.internal;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Instant;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class AuthData {
    public String cookie;
    public String access_token;
    public String refresh_token;
    public int expires_in;
    public long expiryTime;

    @Inject
    public AuthData(@ApplicationContext Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        cookie = sharedPreferences.getString("AuthCookie", null);
        access_token = sharedPreferences.getString("AccessToken", null);
        refresh_token = sharedPreferences.getString("RefreshToken", null);
        expiryTime = sharedPreferences.getLong("ExpiryTime", 0);
    }

    public void saveState(Context context) {
        expiryTime = Instant.now().getEpochSecond() + expires_in - 3; // Just to be safe
        SharedPreferences.Editor editor =
                context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).edit();
        editor.putString("AuthCookie", cookie);
        editor.putString("AccessToken", access_token);
        editor.putString("RefreshToken", refresh_token);
        editor.putLong("ExpiryTime", expiryTime);
        editor.apply();
    }
}
