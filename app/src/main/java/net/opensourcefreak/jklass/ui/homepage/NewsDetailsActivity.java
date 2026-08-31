package net.opensourcefreak.jklass.ui.homepage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.opensourcefreak.jklass.R;
import net.opensourcefreak.jklass.api2.data.homepage.NewsAttachment;
import net.opensourcefreak.jklass.api2.data.homepage.NewsItem;
import net.opensourcefreak.jklass.external.StringCompatUtil;

import java.util.List;

public class NewsDetailsActivity extends AppCompatActivity {
    NewsItem data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_item);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = getIntent().getParcelableExtra("newsItem", NewsItem.class);

        if (data.imagePath.isEmpty()) {
            View coverImageHolder = findViewById(R.id.cover_image_holder);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    coverImageHolder.getLayoutParams()
            );
            layoutParams.setMargins(
                    0,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16,
                            getResources().getDisplayMetrics()
                    ),
                    0,
                    0
            );
            coverImageHolder.setLayoutParams(layoutParams);
        } else
            Glide.with(this).load(data.imagePath)
                .into((ImageView) findViewById(R.id.cover_image));

        ((TextView) findViewById(R.id.title)).setText(data.title);
        ((TextView) findViewById(R.id.body)).setText(data.text.isEmpty() ?
                StringCompatUtil.translateEscapes(data.introText) :
                StringCompatUtil.translateEscapes(data.text)
        );
        ((TextView) findViewById(R.id.author)).setText(data.authorName);
        ((TextView) findViewById(R.id.date))
                .setText(String.format(getString(R.string.published), data.publishedString));
        RecyclerView attachmentsList = findViewById(R.id.attachments);
        attachmentsList.setAdapter(new AttachmentAdapter(data.files));
        attachmentsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {
        private List<NewsAttachment> localDataSet;

        public AttachmentAdapter(List<NewsAttachment> localDataSet) {
            this.localDataSet = localDataSet;
        }

        @NonNull
        @Override
        public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_attachement, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentAdapter.ViewHolder viewHolder, int position) {
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

            public void populateData(NewsAttachment item) {

                int imageResource;
                if (item.isImage)
                    imageResource = R.drawable.image_24px;
                else if (item.isLink)
                    imageResource = R.drawable.link_2_24px;
                else
                    imageResource = R.drawable.attach_file_24px;

                ((ImageView) view.findViewById(R.id.icon)).setImageResource(imageResource);
                ((TextView) view.findViewById(R.id.name)).setText(item.nameWithExtension);

                view.setOnClickListener((view) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.url));
                    startActivity(intent);
                });
            }
        }
    }
}
