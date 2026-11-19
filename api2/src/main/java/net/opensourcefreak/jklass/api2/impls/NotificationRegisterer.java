package net.opensourcefreak.jklass.api2.impls;

import com.google.gson.Gson;

import net.opensourcefreak.jklass.api2.CredentialProvider;
import net.opensourcefreak.jklass.api2.Utils;
import net.opensourcefreak.jklass.api2.data.notification.NotificationRegisterData;
import net.opensourcefreak.jklass.api2.data.notification.UserConfig;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationRegisterer {
    @Inject
    Utils.ThreadUtils threadUtils;
    @Inject
    CredentialProvider credentialProvider;
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();

    @Inject
    NotificationRegisterer() {}

    public void register(String FCMToken) {
        threadUtils.runOnThreadPool(() -> {
            Request request = new Request.Builder()
                    .url("https://api2.vklass.se/v1/configuration/user")
                    .get()
                    .addHeader("Authorization", "Bearer " + credentialProvider.getBearerToken())
                    .build();

            Response response;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Gson gson = new Gson();
            UserConfig userConfig = gson.fromJson(response.body().charStream(), UserConfig.class);

            String json = gson.toJson(
                    new NotificationRegisterData(
                            FCMToken,
                            Arrays.asList(userConfig.userType),
                            userConfig.toggleablePushNotifications
                    )
            );

            request = new Request.Builder(request)
                    .url("https://api2.vklass.se/v1/notifications/push/register")
                    .post(RequestBody.create(json, MediaType.get("application/json")))
                    .build();

            try {
                okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
