package com.nimius.smartbus.views.service.model.bookingModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdultModel implements Parcelable{

    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("quantity")
    @Expose
    public int quantity;
    @SerializedName("used")
    @Expose
    public int used;
    @SerializedName("usedSmart")
    @Expose
    public int usedSmart;

    protected AdultModel(Parcel in) {
        title = in.readString();
        quantity = in.readInt();
        used = in.readInt();
        usedSmart = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(quantity);
        dest.writeInt(used);
        dest.writeInt(usedSmart);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdultModel> CREATOR = new Creator<AdultModel>() {
        @Override
        public AdultModel createFromParcel(Parcel in) {
            return new AdultModel(in);
        }

        @Override
        public AdultModel[] newArray(int size) {
            return new AdultModel[size];
        }
    };
}