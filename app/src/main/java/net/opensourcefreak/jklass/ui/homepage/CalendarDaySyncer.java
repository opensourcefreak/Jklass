package net.opensourcefreak.jklass.ui.homepage;

import androidx.viewpager2.widget.ViewPager2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarDaySyncer {
    // ??????
    public int activeDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
    private final Map<DayChangeListener, Integer> listeners = new HashMap<>();
    private final ViewPager2 viewPager;

    public CalendarDaySyncer(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public void setActiveDay(int day) {
        this.activeDay = day;
        listeners.forEach((listener, page) -> {
            if (viewPager.getCurrentItem() != page)
                listener.onDayUpdate(day);
        });
    }

    public void registerDayChangeListener(DayChangeListener listener, int page) {
        listeners.put(listener, page);
    }

    public interface DayChangeListener {
        void onDayUpdate(int day);
    }
}
