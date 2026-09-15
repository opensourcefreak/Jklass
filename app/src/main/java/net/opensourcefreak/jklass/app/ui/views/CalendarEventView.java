package net.opensourcefreak.jklass.app.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import net.opensourcefreak.jklass.app.R;
import net.opensourcefreak.jklass.api2.data.calendar.Event;

import java.text.SimpleDateFormat;

public class CalendarEventView extends CardView {
    public Event event;

    public CalendarEventView(@NonNull Context context) {
        super(context);
    }

    public CalendarEventView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarEventView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("SetTextI18n")
    public void populateData(Event event) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        this.event = event;

        TextView eventName = findViewById(R.id.event_name);
        eventName.setText(event.title);
        ((TextView) findViewById(R.id.time_text)).setText(
            dateFormat.format(event.start) + " – " + dateFormat.format(event.end)
        );

        String[] locationFields = event.location.split(" \\| ");
        ((TextView) findViewById(R.id.location_text)).setText(locationFields[0]);
        ((TextView) findViewById(R.id.teacher_text)).setText(locationFields[1]);

        if (event.cancelled) {
            findViewById(R.id.event_header)
                    .setBackground(new ColorDrawable(getContext().getColor(R.color.background_warning)));
            eventName.setPaintFlags(eventName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            findViewById(R.id.cancelled_indicator).setVisibility(VISIBLE);
        }
    }
}
