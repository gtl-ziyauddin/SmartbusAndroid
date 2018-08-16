package com.nimius.smartbus.views.service.model.bookingTourModel;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimius.smartbus.views.service.model.bookingModel.PickupPlaceModel;

import java.util.ArrayList;
import java.util.List;

public class BookingTourModel implements Parcelable {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("datetime")
    @Expose
    public String datetime;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("place")
    @Expose
    public PickupPlaceModel place;
    @SerializedName("cars")
    @Expose
    public List<CarModel> cars = new ArrayList<>();
    @SerializedName("activity")
    @Expose
    public ActivityModel activity;

    @SerializedName("bookings")
    @Expose
    public List<BookingTourDetailModel> bookingsList = new ArrayList<>();


    public String errorMessage;

    public BookingTourModel() {

    }


    protected BookingTourModel(Parcel in) {
        url = in.readString();
        id = in.readString();
        status = in.readString();
        date = in.readString();
        time = in.readString();
        datetime = in.readString();
        address = in.readString();
        place = in.readParcelable(PickupPlaceModel.class.getClassLoader());
        cars = in.createTypedArrayList(CarModel.CREATOR);
        activity = in.readParcelable(ActivityModel.class.getClassLoader());
        bookingsList = in.createTypedArrayList(BookingTourDetailModel.CREATOR);
        errorMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(datetime);
        dest.writeString(address);
        dest.writeParcelable(place, flags);
        dest.writeTypedList(cars);
        dest.writeParcelable(activity, flags);
        dest.writeTypedList(bookingsList);
        dest.writeString(errorMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingTourModel> CREATOR = new Creator<BookingTourModel>() {
        @Override
        public BookingTourModel createFromParcel(Parcel in) {
            return new BookingTourModel(in);
        }

        @Override
        public BookingTourModel[] newArray(int size) {
            return new BookingTourModel[size];
        }
    };
}