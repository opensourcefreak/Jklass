package net.opensourcefreak.jklass.api2;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import java.io.InputStream;

import dagger.hilt.EntryPoint;
import dagger.hilt.EntryPoints;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;

@GlideModule
public class GlideAuthModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(
                GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(EntryPoints.get(
                        context.getApplicationContext(),
                        NetworkModuleInterface.class
                ).okHttpClient()));
        Log.d("GlideAuthModule", "registerComponents()");
    }

    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface NetworkModuleInterface {
        OkHttpClient okHttpClient();
    }
}
