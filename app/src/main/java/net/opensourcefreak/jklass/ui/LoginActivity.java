package net.opensourcefreak.jklass.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.opensourcefreak.jklass.R;
import net.opensourcefreak.jklass.api2.LoginCookieProvider;
import net.opensourcefreak.jklass.ui.homepage.MainActivity;

import java.net.URLDecoder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.text.Charsets;
import kotlin.text.Regex;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private WebView vKlassLoginWrapper;
    @Inject
    LoginCookieProvider loginCookieProvider;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        loginCookieProvider.cookieAvailable((cookieAvailable) -> {
            if (cookieAvailable)
                return;

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setContentView(R.layout.activity_login);

        vKlassLoginWrapper = findViewById(R.id.vklass_login_wrapper);

        ViewCompat.setOnApplyWindowInsetsListener(vKlassLoginWrapper, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ((ViewGroup.MarginLayoutParams) v.getLayoutParams())
                    .setMargins(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (vKlassLoginWrapper.canGoBack())
                    vKlassLoginWrapper.goBack();
                else
                    finish();
            }
        });

        /*String urlOverride = getSharedPreferences("LoginMiscData", Context.MODE_PRIVATE)
                .getString("LoginUrlOverride", null);*/

        // Probably not needed but we don't want interference
        CookieManager.getInstance().flush();
        vKlassLoginWrapper.loadUrl("https://auth.vklass.se/oauth2/authorize?client_id=saP/dbUFDhefu/Oyz.api.vklass.se&response_type=code&redirect_uri=vklass://authentication/oauth2/code&scope=https://api.vklass.se/mobile&prompt=select_account&ephemeral=true");//urlOverride == null ?
               // getString(R.string.vklass_login_portal_url) /*: urlOverride*/);
        vKlassLoginWrapper.getSettings().setJavaScriptEnabled(true);
        vKlassLoginWrapper.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                Uri url = request.getUrl();
                String url_string = url.toString();
                /*if (url_string.equals(getString(R.string.vklass_main_page_url))) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }*/

                /*if (url.contains("organisation"))
                    new Thread(() ->
                        getSharedPreferences("LoginMiscData", Context.MODE_PRIVATE)
                                .edit()
                                .putString("LoginUrlOverride", url)
                                .commit()
                    ).start();*/

                /*if (url.equals(getString(R.string.vklass_login_portal_url)))
                    new Thread(() ->
                            getSharedPreferences("LoginMiscData", Context.MODE_PRIVATE)
                                    .edit()
                                    .remove("LoginUrlOverride")
                                    .commit()
                    ).start();*/

                if (url.getScheme().equals("bankid")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    startActivity(intent);
                    // If we return false the WebView tries to open the bankid link
                    // Why? I have no idea. Don't touch.
                    return true;
                } else if (url.getScheme().equals("vklass")) {
                    loginCookieProvider.updateKey(
                            URLDecoder.decode(
                                    new Regex("(?<=code=).+").find(url_string, 0).getValue(),
                                    Charsets.UTF_8
                            )
                    );
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    view.loadUrl(url_string);
                }
                return false;
            }
        });
    }
}