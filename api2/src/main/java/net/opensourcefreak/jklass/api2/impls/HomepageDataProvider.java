package net.opensourcefreak.jklass.api2.impls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.opensourcefreak.jklass.api2.ApiCallback;
import net.opensourcefreak.jklass.api2.Utils;
import net.opensourcefreak.jklass.api2.data.homepage.HomepageData;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomepageDataProvider {
    @Inject
    OkHttpClient okHttpClient;
    @Inject
    Utils.ThreadUtils threadUtils;

    @Inject
    public HomepageDataProvider() {}

    public void buildHomePageData(ApiCallback<HomepageData> callback) {
        threadUtils.runOnThreadPool(() -> {
                    String json;
                    try {
                        Response response = okHttpClient.newCall(
                                new Request.Builder()
                                        .url("https://app.vklass.se/Home/Welcome/")
                                        .build()
                        ).execute();
                        json = Utils.extractVklassJson(response.body().string());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Gson gson = new GsonBuilder().setDateFormat("YYYY-MM-DD").create();
                    HomepageData ret = gson.fromJson(json, HomepageData.class);
                    threadUtils.runOnMainThread(() ->
                            callback.invoke(ret)
                    );
                }
        );
    }
}
