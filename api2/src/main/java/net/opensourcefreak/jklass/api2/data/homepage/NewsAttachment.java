package net.opensourcefreak.jklass.api2.data.homepage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class NewsAttachment implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(nameWithExtension);
        parcel.writeBoolean(isLink);
        parcel.writeBoolean(isImage);
    }

    public static final Parcelable.Creator<NewsAttachment> CREATOR
            = new Parcelable.Creator<>() {
        public NewsAttachment createFromParcel(Parcel in) {
            return new NewsAttachment(in);
        }

        public NewsAttachment[] newArray(int size) {
            return new NewsAttachment[size];
        }
    };

    private NewsAttachment(Parcel parcel) {
        url = parcel.readString();
        nameWithExtension = parcel.readString();
        isLink = parcel.readBoolean();
        isImage = parcel.readBoolean();
    }

    public String url;
    public String nameWithExtension;
    public boolean isLink;
    public boolean isImage;
}
