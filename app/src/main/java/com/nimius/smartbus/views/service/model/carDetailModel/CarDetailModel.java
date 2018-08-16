package com.nimius.smartbus.views.service.model.carDetailModel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimius.smartbus.views.service.model.bookingModel.LocationModel;

public class CarDetailModel implements Parcelable {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("registerDriverUrl")
    @Expose
    public String registerDriverUrl;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("plateNumber")
    @Expose
    public String plateNumber;
    @SerializedName("currentDriver")
    @Expose
    public DriverModel currentDriver;
    @SerializedName("currentLocation")
    @Expose
    public LocationModel currentLocation;
    @SerializedName("currentBusRoute")
    @Expose
    public String currentBusRoute;
    @SerializedName("currentBusStation")
    @Expose
    public String currentBusStation;


    public String errorMessage;


    public CarDetailModel() {

    }


    protected CarDetailModel(Parcel in) {
        url = in.readString();
        registerDriverUrl = in.readString();
        id = in.readString();
        plateNumber = in.readString();
        currentDriver = in.readParcelable(DriverModel.class.getClassLoader());
        currentLocation = in.readParcelable(LocationModel.class.getClassLoader());
        currentBusRoute = in.readString();
        currentBusStation = in.readString();
        errorMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(registerDriverUrl);
        dest.writeString(id);
        dest.writeString(plateNumber);
        dest.writeParcelable(currentDriver, flags);
        dest.writeParcelable(currentLocation, flags);
        dest.writeString(currentBusRoute);
        dest.writeString(currentBusStation);
        dest.writeString(errorMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CarDetailModel> CREATOR = new Creator<CarDetailModel>() {
        @Override
        public CarDetailModel createFromParcel(Parcel in) {
            return new CarDetailModel(in);
        }

        @Override
        public CarDetailModel[] newArray(int size) {
            return new CarDetailModel[size];
        }
    };
}