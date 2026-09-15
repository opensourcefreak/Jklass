package net.opensourcefreak.jklass.app.ui.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.opensourcefreak.jklass.app.R;
import net.opensourcefreak.jklass.api2.impls.CalendarProvider;
import net.opensourcefreak.jklass.app.ui.views.CalendarLayout;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CalendarFragment extends Fragment {
    @Inject
    CalendarProvider calendarProvider;

    public CalendarFragment() {
        super(R.layout.fragment_calendar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager2 viewPager = view.findViewById(R.id.calendar_week_pager);
        viewPager.setAdapter(new WeeksAdapter(viewPager));
        viewPager.setCurrentItem(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR), false);
    }

    public class WeeksAdapter extends RecyclerView.Adapter<WeeksAdapter.ViewHolder> {
        private final CalendarDaySyncer calendarDaySyncer;

        public WeeksAdapter(ViewPager2 viewPager) {
            calendarDaySyncer = new CalendarDaySyncer(viewPager);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View view;

            public ViewHolder(@NonNull View view) {
                super(view);
                this.view = view;
            }

            public void populateData(Calendar week, CalendarDaySyncer calendarDaySyncer, int page) {
                ViewPager2 viewPager = view.findViewById(R.id.calendar_day_pager);
                viewPager.setAdapter(new DaysAdapter(week));
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                                           @Override
                                                           public void onPageSelected(int position) {
                                                               super.onPageSelected(position);

                                                               calendarDaySyncer.setActiveDay(position);
                                                           }
                                                       }
                );

                calendarDaySyncer.registerDayChangeListener((day) -> {
                        viewPager.setCurrentItem(day, false);
                }, page);
                viewPager.setCurrentItem(calendarDaySyncer.activeDay, false);

                ((TextView) view.findViewById(R.id.week_text)).setText(String.format(getString(R.string.week_num_text), week.get(Calendar.WEEK_OF_YEAR)));

                String[] tabTitles = getResources().getStringArray(R.array.week_day_letters);
                TabLayout tabLayout = view.findViewById(R.id.day_tabs);
                new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                        tab.setText(tabTitles[position])
                ).attach();
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.WEEK_OF_YEAR, position);
            holder.populateData(calendar, calendarDaySyncer, position);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_week_page, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return 52;
        }
    }

    public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {
        private Calendar week;

        public DaysAdapter(Calendar week) {
            this.week = week;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_day_page, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Calendar day = (Calendar) week.clone();
            day.set(Calendar.DAY_OF_WEEK, position + 2);
            holder.populateData(day);
        }

        @Override
        public int getItemCount() {
            // 5 workdays per week
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View view;

            public ViewHolder(@NonNull View view) {
                super(view);
                this.view = view;
            }

            public void populateData(Calendar day) {
                calendarProvider.buildCalendarData(day, (calendarData) ->
                    ((CalendarLayout) view.findViewById(R.id.calendar_holder)).populateData(calendarData, day)
                );
            }
        }
    }
}