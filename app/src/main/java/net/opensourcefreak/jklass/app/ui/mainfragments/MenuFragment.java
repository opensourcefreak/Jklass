package net.opensourcefreak.jklass.app.ui.mainfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.opensourcefreak.jklass.api2.data.lunch.LunchItem;
import net.opensourcefreak.jklass.api2.impls.HomepageDataProvider;
import net.opensourcefreak.jklass.app.R;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MenuFragment extends Fragment {
    @Inject
    HomepageDataProvider homepageDataProvider;
    List<LunchItem> lunchItems = null;

    public MenuFragment() {
        super(R.layout.fragment_menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homepageDataProvider.buildLunchData((lunchItems) -> {
                    MenuFragment.this.lunchItems = lunchItems;
                    if (getView() != null)
                        populateData();
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (lunchItems != null) {
            populateData();
        }
        return view;
    }

    private void populateData() {
        View view = getView();
        ((TextView) view.findViewById(R.id.week_text)).setText(
                String.format(getString(R.string.menu_week), Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))
        );
        RecyclerView menuRecycler = view.findViewById(R.id.menu_recycler);
        menuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        menuRecycler.setAdapter(new MenuAdapter(lunchItems));
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuFragment.MenuAdapter.ViewHolder> {
        private List<LunchItem> localDataSet;

        public MenuAdapter(List<LunchItem> localDataSet) {
            this.localDataSet = localDataSet;
        }

        @NonNull
        @Override
        public MenuFragment.MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuFragment.MenuAdapter.ViewHolder viewHolder, int position) {
            viewHolder.populateData(localDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return localDataSet.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private View view;

            public ViewHolder(@NonNull View view) {
                super(view);
                this.view = view;
            }

            public void populateData(LunchItem item) {
                StringBuilder menu = new StringBuilder();
                item.menuItems.forEach(menu::append);
                ((TextView) view.findViewById(R.id.menu_text)).setText(menu.toString());
                int dayOfWeek = item.date.toInstant()
                        .atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();
                ((TextView) view.findViewById(R.id.day_letter)).setText(
                    getResources().getStringArray(R.array.week_day_letters)
                            [dayOfWeek - 1]
                );
                if (dayOfWeek == Instant.now().atZone(ZoneId.systemDefault()).getDayOfWeek().getValue())
                    view.findViewById(R.id.today_highlight)
                            .setBackgroundColor(getContext().getColor(R.color.background_colourful));
            }
        }
    }
}
