package net.opensourcefreak.jklass.api2.impls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import net.opensourcefreak.jklass.api2.ApiCallback;
import net.opensourcefreak.jklass.api2.Utils;
import net.opensourcefreak.jklass.api2.data.homepage.HomepageData;
import net.opensourcefreak.jklass.api2.data.lunch.LunchItem;

import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Singleton
public class HomepageDataProvider {
    @Inject
    OkHttpClient okHttpClient;
    @Inject
    Utils.ThreadUtils threadUtils;
    String jsonData;
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Inject
    HomepageDataProvider() {}

    public void buildHomePageData(ApiCallback<HomepageData> callback) {
        threadUtils.runOnThreadPool(() -> {
                prepareJsonDataLazy();

                HomepageData ret = gson.fromJson(jsonData, HomepageData.class);
                threadUtils.runOnMainThread(() ->
                    callback.invoke(ret)
                );
            }
        );
    }

    public void buildLunchData(ApiCallback<List<LunchItem>> callback) {
        threadUtils.runOnThreadPool(() -> {
            prepareJsonDataLazy();

            JsonElement lunchAsElement = JsonParser.parseString(jsonData)
                    .getAsJsonObject()
                    .getAsJsonArray("schoolLunchItems")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonArray("lunchItems");
            List<LunchItem> lunchItems = gson.fromJson(
                    lunchAsElement,
                    TypeToken.getParameterized(List.class, LunchItem.class).getType()
                );
            int curWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            List<LunchItem> ret = lunchItems.stream().filter((item -> item.id != -1 &&
                    item.date.toInstant().atZone(ZoneId.systemDefault()).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == curWeek)
            ).collect(Collectors.toList());
            threadUtils.runOnMainThread(() ->
                    callback.invoke(ret)
                );
            }
        );
    }

    private void prepareJsonDataLazy() {
        if (jsonData != null)
            return;

        try {
            Response response = okHttpClient.newCall(
                    new Request.Builder()
                            .url("https://app.vklass.se/Home/Welcome/")
                            .build()
            ).execute();
            ResponseBody body = response.body();
            jsonData = Utils.extractVklassJson(body.string());
            body.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
