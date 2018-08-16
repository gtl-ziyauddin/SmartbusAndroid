package com.nimius.smartbus.views.service.model.bookingModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgeGroupsModel implements Parcelable {

    @SerializedName("ADULT")
    @Expose
    public AdultModel adultModel;

    protected AgeGroupsModel(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AgeGroupsModel> CREATOR = new Creator<AgeGroupsModel>() {
        @Override
        public AgeGroupsModel createFromParcel(Parcel in) {
            return new AgeGroupsModel(in);
        }

        @Override
        public AgeGroupsModel[] newArray(int size) {
            return new AgeGroupsModel[size];
        }
    };
}