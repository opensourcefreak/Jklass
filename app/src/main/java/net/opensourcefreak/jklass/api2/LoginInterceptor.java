package net.opensourcefreak.jklass.api2;

import androidx.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoginInterceptor implements Interceptor {
    private LoginCookieProvider loginCookieProvider;
    private String cookie;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        request = applyAuthCookie(request);

        Response response = chain.proceed(request);
        if (response.code() == 302) {
            response.close();
            cookie = loginCookieProvider.getCookie();
            request = applyAuthCookie(request);
            return chain.proceed(request);
        }

        return response;
    }

    private Request applyAuthCookie(Request request) {
        return request.newBuilder()
                .header("Cookie", "se.vklass.authentication=" + cookie + ";")
                .build();
    }

    @Inject
    public LoginInterceptor(LoginCookieProvider loginCookieProvider, Utils.ThreadUtils threadUtils) {
        this.loginCookieProvider = loginCookieProvider;
        /*threadUtils.runOnThreadPool(() ->
            cookie = loginCookieProvider.getCookie()
        );*/
    }
}
