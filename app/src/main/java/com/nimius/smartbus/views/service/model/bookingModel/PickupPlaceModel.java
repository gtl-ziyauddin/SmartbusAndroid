package com.nimius.smartbus.views.service.model.bookingModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PickupPlaceModel implements Parcelable {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("location")
    @Expose
    public LocationModel location;
    @SerializedName("description")
    @Expose
    public String description;

    protected PickupPlaceModel(Parcel in) {
        id = in.readInt();
        url = in.readString();
        title = in.readString();
        location = in.readParcelable(LocationModel.class.getClassLoader());
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeParcelable(location, flags);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PickupPlaceModel> CREATOR = new Creator<PickupPlaceModel>() {
        @Override
        public PickupPlaceModel createFromParcel(Parcel in) {
            return new PickupPlaceModel(in);
        }

        @Override
        public PickupPlaceModel[] newArray(int size) {
            return new PickupPlaceModel[size];
        }
    };
}
