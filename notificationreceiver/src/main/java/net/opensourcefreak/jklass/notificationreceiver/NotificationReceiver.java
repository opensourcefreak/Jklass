package net.opensourcefreak.jklass.notificationreceiver;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

import net.opensourcefreak.jklass.api2.CredentialProvider;
import net.opensourcefreak.jklass.api2.impls.NotificationRegisterer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationReceiver extends FirebaseMessagingService {
    @Inject
    NotificationRegisterer notificationRegisterer;
    @Inject
    CredentialProvider credentialProvider;

    @Override
    public void onNewToken(@NonNull String token) {
        if (credentialProvider.isLoggedIn())
            notificationRegisterer.register(token);
        else
            credentialProvider.registerOnLoggedInCallback((isLoggedIn) -> {
                if (isLoggedIn)
                    notificationRegisterer.register(token);
            });
    }
}