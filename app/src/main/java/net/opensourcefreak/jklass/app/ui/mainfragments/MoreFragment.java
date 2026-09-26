package net.opensourcefreak.jklass.app.ui.mainfragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.opensourcefreak.jklass.app.R;

public class MoreFragment extends NavigationFragment {
    private final Entry[] entries = {
        new Entry(
                R.id.item_menu,
                (view) ->
                    getNavController().navigate(R.id.action_open_menu),
                R.string.menu,
                R.drawable.chef_hat_24px
        )
    };

    public MoreFragment() {
        super(R.layout.fragment_more);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (Entry entry : entries) {
            entry.apply(view);
        }
    }

    private static class Entry {
        private final int target;
        private final View.OnClickListener onClickListener;
        private final int textResId;
        private final int iconResId;

        public Entry(int target, View.OnClickListener onClickListener, int textResId, int iconResId) {
            this.target = target;
            this.onClickListener = onClickListener;
            this.textResId = textResId;
            this.iconResId = iconResId;
        }

        public void apply(View root) {
            View view = root.findViewById(target);

            ((TextView) view.findViewById(R.id.item_description)).setText(textResId);
            ((ImageView) view.findViewById(R.id.item_image)).setImageResource(iconResId);
            view.setOnClickListener(onClickListener);
        }
    }
}
