package net.opensourcefreak.jklass.notificationreceiver;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public abstract class NotificationReceiverInitializer {
    public static void initialize(Context context) {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId(context.getString(R.string.fcm_projectid))
                .setApiKey(context.getString(R.string.fcm_apikey))
                .setApplicationId(context.getString(R.string.fcm_appid))
                .setGcmSenderId(context.getString(R.string.fcm_senderid))
                .setDatabaseUrl(context.getString(R.string.fcm_databaseurl))
                .setStorageBucket(context.getString(R.string.fcm_storagebucket))
                .build();

        FirebaseApp.initializeApp(context, options);
    }
}
