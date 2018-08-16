package com.nimius.smartbus.views.service.model.bookingTourModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimius.smartbus.views.service.model.bookingModel.AgeGroupsModel;

import java.util.List;

public class BookingTourDetailModel implements Parcelable{

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("ageGroups")
    @Expose
    public AgeGroupsModel ageGroups;
    @SerializedName("dropoffAddress")
    @Expose
    public Object dropoffAddress;
    @SerializedName("created")
    @Expose
    public String created;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("paidType")
    @Expose
    public String paidType;
    @SerializedName("customerId")
    @Expose
    public int customerId;
    @SerializedName("customerName")
    @Expose
    public String customerName;
    @SerializedName("customerEmail")
    @Expose
    public String customerEmail;

    protected BookingTourDetailModel(Parcel in) {
        id = in.readInt();
        url = in.readString();
        code = in.readString();
        ageGroups = in.readParcelable(AgeGroupsModel.class.getClassLoader());
        created = in.readString();
        status = in.readString();
        paidType = in.readString();
        customerId = in.readInt();
        customerName = in.readString();
        customerEmail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(code);
        dest.writeParcelable(ageGroups, flags);
        dest.writeString(created);
        dest.writeString(status);
        dest.writeString(paidType);
        dest.writeInt(customerId);
        dest.writeString(customerName);
        dest.writeString(customerEmail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingTourDetailModel> CREATOR = new Creator<BookingTourDetailModel>() {
        @Override
        public BookingTourDetailModel createFromParcel(Parcel in) {
            return new BookingTourDetailModel(in);
        }

        @Override
        public BookingTourDetailModel[] newArray(int size) {
            return new BookingTourDetailModel[size];
        }
    };
}
