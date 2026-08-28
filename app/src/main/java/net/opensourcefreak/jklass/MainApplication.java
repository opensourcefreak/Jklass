package net.opensourcefreak.jklass;

import android.app.Application;

import net.opensourcefreak.jklass.api2.LoginCookieProvider;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
