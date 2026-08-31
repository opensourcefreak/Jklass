package net.opensourcefreak.jklass.api2.data.homepage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Parcelable needs to be implemented for viewing news details
// ... at some point
public class NewsItem implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeLong(publishDate.getTime());
        parcel.writeString(title);
        parcel.writeString(authorName);
        parcel.writeString(introText);
        parcel.writeString(text);
        parcel.writeString(publishedString);
        parcel.writeParcelableList(files, flags);
        parcel.writeString(imagePath);
    }

    public static final Parcelable.Creator<NewsItem> CREATOR
            = new Parcelable.Creator<>() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    private NewsItem(Parcel parcel) {
        publishDate = new Date(parcel.readLong());
        title = parcel.readString();
        authorName = parcel.readString();
        introText = parcel.readString();
        text = parcel.readString();
        publishedString = parcel.readString();
        files = parcel.readParcelableList(
                new ArrayList<>(),
                NewsAttachment.class.getClassLoader(),
                NewsAttachment.class
        );
        imagePath = parcel.readString();
    }

    public List<NewsAttachment> files;
    public Date publishDate;
    public String title;
    public String authorName;
    public String introText;
    public String text;
    public String imagePath;

    // Not necessary but I choose to include this for convenience
    public String publishedString;
}
