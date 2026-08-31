package net.opensourcefreak.jklass.api2.impls;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.opensourcefreak.jklass.api2.Utils;
import net.opensourcefreak.jklass.api2.data.calendar.Event;
import net.opensourcefreak.jklass.api2.ApiCallback;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.text.Charsets;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalendarProvider {
    @Inject
    Utils.ThreadUtils threadUtils;
    @Inject
    OkHttpClient okHttpClient;

    @Inject
    public CalendarProvider() {}

    public void buildCalendarData(final Calendar day, final ApiCallback<List<Event>> callback) {
        threadUtils.runOnThreadPool(() -> {
                Calendar mDay = day;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                mDay = (Calendar) mDay.clone();
                // If you set it too low it will fetch yesterday's events too, 4 is somewhat of a sweet spot
                mDay.set(Calendar.HOUR, 4);
                mDay.set(Calendar.MINUTE, 0);
                mDay.set(Calendar.SECOND, 0);
                mDay.set(Calendar.AM_PM, 0);

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();

                String startDate = dateFormat.format(mDay.getTime());
                mDay.set(Calendar.HOUR, 24);

                Request request = new Request.Builder()
                        .url("https://app.vklass.se/Events/FullCalendar")
                        .post(new FormBody.Builder()
                                .add("start", startDate)
                                .add("end", dateFormat.format(mDay.getTime()))
                                .build()
                        )
                        .build();

                Response response;
                try {
                    response = okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                List<Event> ret = gson.fromJson(new InputStreamReader(response.body().byteStream()),
                        TypeToken.getParameterized(List.class, Event.class).getType()
                );

                threadUtils.runOnMainThread( () ->
                    callback.invoke(ret)
                );
            }
        );
    }
}
