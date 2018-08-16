package com.nimius.smartbus.views.service.model.bookingModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BookingModel implements Parcelable {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("pickupAddress")
    @Expose
    public String pickupAddress;
    @SerializedName("dropoffAddress")
    @Expose
    public String dropoffAddress;
    @SerializedName("pickupPlace")
    @Expose
    public PickupPlaceModel pickupPlace;

    @SerializedName("tour")
    @Expose
    public String tour;
    @SerializedName("smartTour")
    @Expose
    public String smartTour;
    @SerializedName("ageGroups")
    @Expose
    public AgeGroupsModel ageGroups;

    @SerializedName("created")
    @Expose
    public String created;
    @SerializedName("activity")
    @Expose
    public String activity;
    @SerializedName("startDateTime")
    @Expose
    public String startDateTime;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("startTime")
    @Expose
    public String startTime;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("paymentStatus")
    @Expose
    public String paymentStatus;
    @SerializedName("customerId")
    @Expose
    public int customerId;
    @SerializedName("customerName")
    @Expose
    public String customerName;
    @SerializedName("customerLastName")
    @Expose
    public String customerLastName;
    @SerializedName("customerEmail")
    @Expose
    public String customerEmail;

    public String errorMessage;

    public BookingModel() {

    }

    protected BookingModel(Parcel in) {
        id = in.readInt();
        url = in.readString();
        code = in.readString();
        pickupAddress = in.readString();
        dropoffAddress = in.readString();
        pickupPlace = in.readParcelable(PickupPlaceModel.class.getClassLoader());
        tour = in.readString();
        smartTour = in.readString();
        ageGroups = in.readParcelable(AgeGroupsModel.class.getClassLoader());
        created = in.readString();
        activity = in.readString();
        startDateTime = in.readString();
        startDate = in.readString();
        startTime = in.readString();
        status = in.readString();
        paymentStatus = in.readString();
        customerId = in.readInt();
        customerName = in.readString();
        customerLastName = in.readString();
        customerEmail = in.readString();
        errorMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(code);
        dest.writeString(pickupAddress);
        dest.writeString(dropoffAddress);
        dest.writeParcelable(pickupPlace, flags);
        dest.writeString(tour);
        dest.writeString(smartTour);
        dest.writeParcelable(ageGroups, flags);
        dest.writeString(created);
        dest.writeString(activity);
        dest.writeString(startDateTime);
        dest.writeString(startDate);
        dest.writeString(startTime);
        dest.writeString(status);
        dest.writeString(paymentStatus);
        dest.writeInt(customerId);
        dest.writeString(customerName);
        dest.writeString(customerLastName);
        dest.writeString(customerEmail);
        dest.writeString(errorMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingModel> CREATOR = new Creator<BookingModel>() {
        @Override
        public BookingModel createFromParcel(Parcel in) {
            return new BookingModel(in);
        }

        @Override
        public BookingModel[] newArray(int size) {
            return new BookingModel[size];
        }
    };
}
