package net.opensourcefreak.jklass.api2;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;

@Module
@InstallIn(SingletonComponent.class)
public abstract class OkHttpClientProvider {
    @Singleton
    @Provides
    public static OkHttpClient getOkHttpClient(LoginInterceptor loginInterceptor) {
        return new OkHttpClient.Builder()
                    .followRedirects(false)
                    .addInterceptor(loginInterceptor)
                    .build();
    }
}
