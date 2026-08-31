package net.opensourcefreak.jklass.app.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.opensourcefreak.jklass.api2.data.calendar.Event;
import net.opensourcefreak.jklass.app.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarLayout extends ViewGroup {
    // 10h Unix time milliseconds
    private static final long TOTAL_TIME = 36000000L;
    private Date startDate;
    private final int child_margins = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        12,
        getResources().getDisplayMetrics());

    public CalendarLayout(Context context) {
        super(context);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int countChildren = getChildCount();

        Rect childLayoutRect = new Rect();
        for (int i = 0; i < countChildren; i++) {
            net.opensourcefreak.jklass.app.views.CalendarEventView child = (net.opensourcefreak.jklass.app.views.CalendarEventView) getChildAt(i);

            childLayoutRect.right = right - child_margins;
            childLayoutRect.left = left + child_margins;

            // I luv math
            int height = getMeasuredHeight();
            float startFloat = (float) (child.event.start.getTime() - startDate.getTime()) / TOTAL_TIME;
            float endFloat = (float) (child.event.end.getTime() - startDate.getTime()) / TOTAL_TIME;
            childLayoutRect.top = (int) (startFloat * height);
            childLayoutRect.bottom = (int) (endFloat * height);

            child.layout(childLayoutRect.left, childLayoutRect.top,
                    childLayoutRect.right, childLayoutRect.bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                width - child_margins * 2,
                MeasureSpec.EXACTLY
        );

        for (int i = 0; i < getChildCount(); i++) {
            net.opensourcefreak.jklass.app.views.CalendarEventView child = (net.opensourcefreak.jklass.app.views.CalendarEventView) getChildAt(i);
            float childHeightFraction =
                    ((float) (child.event.end.getTime() - startDate.getTime()) / TOTAL_TIME) -
                            ((float) (child.event.start.getTime() - startDate.getTime()) / TOTAL_TIME);

            child.measure(
                    childWidthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(
                            (int) (childHeightFraction * height),
                            MeasureSpec.EXACTLY
                    )
            );
        }
    }


    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void populateData(List<Event> data, Calendar day) {
        day = (Calendar) day.clone();
        day.set(Calendar.AM_PM, 0);
        day.set(Calendar.HOUR, 8);
        day.set(Calendar.MINUTE, 0);
        startDate = day.getTime();

        data.forEach((event -> {
            // If it's not 1 then it's trash
            if (event.eventType == 1) {
                net.opensourcefreak.jklass.app.views.CalendarEventView v = (net.opensourcefreak.jklass.app.views.CalendarEventView) LayoutInflater.from(getContext()).inflate(R.layout.calendar_event, CalendarLayout.this, false);
                addView(
                        v
                );
                v.populateData(event);
            }
        }));
    }
}
