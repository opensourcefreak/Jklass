package net.opensourcefreak.jklass.api2;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import net.opensourcefreak.jklass.api2.data.internal.AuthResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.text.Charsets;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

@Singleton
public class LoginCookieProvider {
    private String authToken;
    private final Context context;
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();
    @Inject
    Utils.ThreadUtils threadUtils;

    public void cookieAvailable(ApiCallback<Boolean> callback) {
        threadUtils.runOnThreadPool(() -> {
            Boolean ret = getCookie() == null;
            threadUtils.runOnMainThread(() ->
                    callback.invoke(ret)
            );
        });
    }

    @Nullable
    public String getCookie() {
        if (authToken == null)
            return null;

        Request request = new Request.Builder()
                .url("https://auth.vklass.se/oauth2/exchange/cookie?targetUrl=https%3A%2F%2Fapp.vklass.se%2F")
                // This is very incorrect, all the fields should be encoded but
                // vklass does not support that
                .post(RequestBody.create(
                        "token=" + authToken + "&" +
                                "token_type_hint=access_token&" +
                                "client_id=saP/dbUFDhefu/Oyz.api.vklass.se&" +
                                "client_secret=AL34uIE23L5KUF45vEW/uKQFdjdsjknsdkjsu7AF7eqgsddsdje3d",
                        MediaType.get("application/x-www-form-urlencoded")
                    )
                )
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }

        MatchResult cookieMatch = new Regex("(?<=se\\.vklass\\.authentication=)[^ \\n;]+(?=;)").find(
                response.header("Set-Cookie"),
                0
        );

            Log.d("cook", response.header("Set-Cookie"));

        return cookieMatch == null ?
                null :
                cookieMatch.getValue();
    }

    public void updateKey(String authToken) {
        threadUtils.runOnThreadPool(() -> {
            try {
                String response = okHttpClient.newCall( new Request.Builder()
                        .url("https://auth.vklass.se/oauth2/token")
                        .post(new FormBody.Builder()
                                .add("code", authToken)
                                .add("redirect_uri", "vklass://authentication/oauth2/code")
                                .add("client_id", "saP/dbUFDhefu/Oyz.api.vklass.se")
                                .add("grant_type", "authorization_code")
                                .build()
                        )
                        .header("Authorization", "Basic c2FQL2RiVUZEaGVmdS9PeXouYXBpLnZrbGFzcy5zZTpBTDM0dUlFMjNMNUtVRjQ1dkVXL3VLUUZkamRzamtuc2RranN1N0FGN2VxZ3NkZHNkamUzZA==")
                        .build()
                ).execute().body().string();
                Log.d("r", response);
                this.authToken = new Gson().fromJson(
                        response,
                        AuthResponse.class
                ).access_token;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            context.getSharedPreferences("AuthData", MODE_PRIVATE)
                    .edit().putString("AuthToken", this.authToken).apply();
            }
        );
    }

    @Inject
    public LoginCookieProvider(@ApplicationContext Context context) {
        this.context = context;
        authToken = context.getSharedPreferences("AuthData", MODE_PRIVATE)
                .getString("AuthToken", null);
    }
}
