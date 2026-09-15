package net.opensourcefreak.jklass.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import net.opensourcefreak.jklass.api2.CredentialProvider;
import net.opensourcefreak.jklass.api2.data.calendar.Event;
import net.opensourcefreak.jklass.api2.impls.CalendarProvider;
import net.opensourcefreak.jklass.app.R;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Comparator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NextEventAppWidgetProvider extends AppWidgetProvider {
    @Inject
    CalendarProvider calendarProvider;
    @Inject
    CredentialProvider credentialProvider;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final Handler handler = new Handler(Looper.getMainLooper());
        Log.d("uwu", "updating widgets");
        boolean widgetIsEmpty = false;
        int widgetText = 0;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities caps =
                connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (!credentialProvider.isLoggedIn()) {
            widgetIsEmpty = true;
            widgetText = R.string.widget_not_logged_in;
        } else if (caps == null ||
                !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
                !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            widgetIsEmpty = true;
            widgetText = R.string.no_internet;
        }
        if (widgetIsEmpty) {
            loadEmptyWidget(context, appWidgetManager, appWidgetIds, widgetText);
            return;
        }
        Calendar today = Calendar.getInstance();
        calendarProvider.buildCalendarData(today,
            (data) -> {
                data.sort(
                        Comparator.comparingLong(x -> x.start.getTime())
                );

                Event event = null;
                long now = Instant.now().toEpochMilli();
                for (Event item : data) {
                    if (now < item.start.getTime()) {
                        event = item;
                        break;
                    }
                }

                if (event == null) {
                    loadEmptyWidget(context, appWidgetManager, appWidgetIds, R.string.widget_empty);
                    return;
                }

                String[] locationFields = event.location.split(" \\| ");

                for (int id : appWidgetIds) {
                    RemoteViews remoteViews =
                            new RemoteViews(context.getPackageName(),
                                    R.layout.widget_next_event);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    remoteViews.setTextViewText(R.id.event_name, event.title);
                    remoteViews.setTextViewText(R.id.time_text,
                            dateFormat.format(event.start) + " – " +
                                    dateFormat.format(event.end));

                    remoteViews.setTextViewText(R.id.location_text, locationFields[0]);
                    remoteViews.setTextViewText(R.id.teacher_text, locationFields[1]);

                    appWidgetManager.updateAppWidget(id, remoteViews);
                }

                handler.postAtTime(
                    () -> {
                        Intent intent = new Intent(
                                context.getApplicationContext(),
                                NextEventAppWidgetProvider.class
                        );
                        intent.setAction(
                                AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        );
                        AppWidgetManager widgetManager =
                                AppWidgetManager.getInstance(context);
                        int[] ids = widgetManager.getAppWidgetIds(
                                new ComponentName(
                                        context,
                                        NextEventAppWidgetProvider.class
                                )
                        );
                        widgetManager
                                .notifyAppWidgetViewDataChanged(ids, android.R.id.list);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                        context.sendBroadcast(intent);
                    },
                    event.start.getTime() + 5000 // Just in case
                );
            }
        );
    }

    private void loadEmptyWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, int textResId) {
        RemoteViews remoteViews =
                new RemoteViews(context.getPackageName(), R.layout.widget_empty);
        remoteViews.setTextViewText(R.id.text, context.getText(textResId));
        for (int id : appWidgetIds)
            appWidgetManager.updateAppWidget(id, remoteViews);
    }
}
