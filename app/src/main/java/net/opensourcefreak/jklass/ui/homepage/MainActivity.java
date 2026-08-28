package net.opensourcefreak.jklass.ui.homepage;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.opensourcefreak.jklass.MainApplication;
import net.opensourcefreak.jklass.R;
import net.opensourcefreak.jklass.api2.LoginCookieProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    LoginCookieProvider loginCookieProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Fragment[] mainFragments = {
                ((FragmentContainerView) findViewById(R.id.content_fragment)).getFragment(),
                new CalendarFragment(),
        };

        FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener((menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.feed) {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_fragment, (Fragment) mainFragments[0])
                        .setTransition(TRANSIT_FRAGMENT_FADE)
                        .commit();
            } else if (id == R.id.schedule) {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_fragment, (Fragment) mainFragments[1])
                        .setTransition(TRANSIT_FRAGMENT_FADE)
                        .commit();
            }

            return true;
        }));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }
}