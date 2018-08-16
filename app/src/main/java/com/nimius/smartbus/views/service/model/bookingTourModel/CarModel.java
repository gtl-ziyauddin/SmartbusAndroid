package com.nimius.smartbus.views.service.model.bookingTourModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarModel implements Parcelable {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("plateNumber")
    @Expose
    public String plateNumber;

    protected CarModel(Parcel in) {
        url = in.readString();
        id = in.readString();
        plateNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(id);
        dest.writeString(plateNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CarModel> CREATOR = new Creator<CarModel>() {
        @Override
        public CarModel createFromParcel(Parcel in) {
            return new CarModel(in);
        }

        @Override
        public CarModel[] newArray(int size) {
            return new CarModel[size];
        }
    };
}
