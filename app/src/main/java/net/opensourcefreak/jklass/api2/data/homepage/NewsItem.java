package net.opensourcefreak.jklass.api2.data.homepage;

import java.util.Date;

// Parcelable needs to be implemented for viewing news details
// ... at some point
public class NewsItem /*implements Parcelable*/ {
    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {

    }*/

    public Date publishDate;
    public String title;
    public String authorName;
    public String introText;
    public String text;

    // Not necessary but i choose to include this for convenience
    public String publishedString;
}
