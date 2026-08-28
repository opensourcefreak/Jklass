package net.opensourcefreak.jklass.api2;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlin.text.Regex;

public abstract class Utils {
    public static String extractVklassJson(String item) {
        String json = new Regex("enhancer\\.enhanceServerHtml\\([^\\{\\}]+((?:\\{).+(?:\\}))[^)]+\\)")
                .find(item, 0).getGroups().get(1).getValue();

        // Since the JSON is hardcoded inside a js string *sigh*, we need to unescape the escapes
        return json.replace("\\\\", "\\");
    }

    @Singleton
    public static class ThreadUtils {
        private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                        4,
                        20,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>()
        );
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        public void runOnThreadPool(Runnable r) {
            threadPoolExecutor.execute(r);
        }

        public void runOnMainThread(Runnable r) {
            mainThreadHandler.post(r);
        }

        @Inject
        public ThreadUtils() {
        }
    }
}
