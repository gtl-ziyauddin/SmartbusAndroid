package com.nimius.smartbus.views.service.model.bookingTourModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityModel implements Parcelable {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("direction")
    @Expose
    public String direction;
    @SerializedName("minPassengers")
    @Expose
    public int minPassengers;
    @SerializedName("maxPassengers")
    @Expose
    public int maxPassengers;

    protected ActivityModel(Parcel in) {
        url = in.readString();
        name = in.readString();
        type = in.readString();
        direction = in.readString();
        minPassengers = in.readInt();
        maxPassengers = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(direction);
        dest.writeInt(minPassengers);
        dest.writeInt(maxPassengers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActivityModel> CREATOR = new Creator<ActivityModel>() {
        @Override
        public ActivityModel createFromParcel(Parcel in) {
            return new ActivityModel(in);
        }

        @Override
        public ActivityModel[] newArray(int size) {
            return new ActivityModel[size];
        }
    };
}