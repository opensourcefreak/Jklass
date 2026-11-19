package net.opensourcefreak.jklass.api2;

import android.content.Context;

import com.google.gson.Gson;

import net.opensourcefreak.jklass.api2.data.internal.AuthData;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.text.Regex;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
public class CredentialProvider {
    private AuthData authData;
    @Inject
    @ApplicationContext
    Context context;
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();
    private Utils.ThreadUtils threadUtils;
    private ArrayList<ApiCallback<Boolean>> loginCallbacks = new ArrayList();

    @Inject
    public CredentialProvider(Utils.ThreadUtils threadUtils, AuthData authData) {
        this.threadUtils = threadUtils;
        this.authData = authData;
        threadUtils.runOnThreadPool(() -> {
            if (!isLoggedIn())
                return;

            refreshTokens();
        });
    }

    public void registerOnLoggedInCallback(ApiCallback<Boolean> callback) {
        loginCallbacks.add(callback);
    }

    public boolean isLoggedIn() {
        return authData.expiryTime > Instant.now().getEpochSecond();
    }

    @Nullable
    public String getCookie() {
        return authData.cookie;
    }

    @Nullable
    public String getBearerToken() {
        return authData.access_token;
    }

    public void refreshCookie() {
        Request request = new Request.Builder()
                .url("https://auth.vklass.se/oauth2/exchange/cookie?targetUrl=https%3A%2F%2Fapp.vklass.se%2F")
                // This is very incorrect, all the fields should be encoded but
                // vklass does not support that
                .post(RequestBody.create(
                                "token=" + authData.access_token + "&" +
                                        "token_type_hint=access_token&" +
                                        "client_id=" + Utils.CLIENT_ID + "&" +
                                        "client_secret=" + Utils.CLIENT_SECRET,
                                MediaType.get("application/x-www-form-urlencoded")
                        )
                )
                .build();

        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        authData.cookie = new Regex("(?<=se\\.vklass\\.authentication=)[^ \\n;]+(?=;)").find(
                response.header("Set-Cookie"),
                0
        ).getValue();
    }

    private void refreshTokens() {
        AuthData authDataTmp;
        try {
            String response = okHttpClient.newCall( new Request.Builder()
                    .url("https://auth.vklass.se/oauth2/token")
                    .post(new FormBody.Builder()
                            .add("code", authData.refresh_token)
                            .add("refresh_token", authData.refresh_token)
                            .add("redirect_uri", "vklass://authentication/oauth2/code")
                            .add("client_id", Utils.CLIENT_ID)
                            .add("grant_type", "refresh_token")
                            .build()
                    )
                    .header("Authorization", Credentials.basic(Utils.CLIENT_ID, Utils.CLIENT_SECRET))
                    .build()
            ).execute().body().string();
            authDataTmp = new Gson().fromJson(
                    response,
                    AuthData.class
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        authDataTmp.refresh_token = authData.refresh_token;
        authDataTmp.saveState(context);
        authData = authDataTmp;
        refreshCookie();
    }

    public void login(String refreshToken, ApiCallback<Object> callback) {
        threadUtils.runOnThreadPool(() -> {
            authData.refresh_token = refreshToken;
            try {
                String response = okHttpClient.newCall( new Request.Builder()
                        .url("https://auth.vklass.se/oauth2/token")
                        .post(new FormBody.Builder()
                                .add("code", authData.refresh_token)
                                .add("redirect_uri", "vklass://authentication/oauth2/code")
                                .add("client_id", Utils.CLIENT_ID)
                                .add("grant_type", "authorization_code")
                                .build()
                        )
                        .header("Authorization", Credentials.basic(Utils.CLIENT_ID, Utils.CLIENT_SECRET))
                        .build()
                ).execute().body().string();
                authData = new Gson().fromJson(
                        response,
                        AuthData.class
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            authData.saveState(context);
            refreshCookie();
            threadUtils.runOnMainThread(() -> {
                    callback.invoke(null);
                    Boolean mIsLoggedIn = isLoggedIn();
                    loginCallbacks.forEach((loginCallback) -> {
                        loginCallback.invoke(mIsLoggedIn);
                    });
                }
            );
        });
    }
}
