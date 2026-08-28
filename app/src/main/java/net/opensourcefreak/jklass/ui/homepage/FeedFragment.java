package net.opensourcefreak.jklass.ui.homepage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.opensourcefreak.jklass.R;
import net.opensourcefreak.jklass.api2.data.homepage.HomepageData;
import net.opensourcefreak.jklass.api2.data.homepage.NewsItem;
import net.opensourcefreak.jklass.api2.impls.HomepageDataProvider;
import net.opensourcefreak.jklass.external.StringCompatUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FeedFragment extends Fragment {
    RecyclerView newsList;
    HomepageData homepageData;

    @Inject
    HomepageDataProvider homepageDataProvider;

    public FeedFragment() {
        super(R.layout.fragment_home_feed);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homepageDataProvider.buildHomePageData((homepageData) -> {
            this.homepageData = homepageData;
            populateData();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();

        newsList = view.findViewById(R.id.news_list);
        newsList.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        if (homepageData != null) {
            populateData();
        }
    }

    private void populateData() {
        TextView greeting = getView().findViewById(R.id.geeting);

        greeting.setText(String.format(getString(R.string.greeting), homepageData.firstName));
        newsList.setAdapter(new NewsAdapter(homepageData.newsItems));
    }

    public class NewsAdapter extends RecyclerView.Adapter {
        private final List<NewsItem> localDataSet;

        public NewsAdapter(List<NewsItem> localDataSet) {
            this.localDataSet = localDataSet;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView bodyText;
            private final TextView titleText;

            public ViewHolder(@NonNull View view) {
                super(view);

                view.setOnClickListener((v) ->
                        Toast.makeText(getActivity(), "Unimplemented", Toast.LENGTH_SHORT).show()
                );

                bodyText = view.findViewById(R.id.body);
                titleText = view.findViewById(R.id.title);
            }

            public void populateData(NewsItem data) {
                titleText.setText(StringCompatUtil.translateEscapes(data.title));
                bodyText.setText(data.text.isEmpty() ?
                        StringCompatUtil.translateEscapes(data.introText) :
                        StringCompatUtil.translateEscapes(data.text)
                );
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            ((ViewHolder) viewHolder).populateData(localDataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }
}