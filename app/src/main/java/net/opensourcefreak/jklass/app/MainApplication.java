package net.opensourcefreak.jklass.app;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

import net.opensourcefreak.jklass.notificationreceiver.NotificationReceiverInitializer;;

@HiltAndroidApp
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationReceiverInitializer.initialize(this);
    }
}
