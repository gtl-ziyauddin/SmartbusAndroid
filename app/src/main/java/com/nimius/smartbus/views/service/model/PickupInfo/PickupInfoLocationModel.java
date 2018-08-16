package com.nimius.smartbus.views.service.model.PickupInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PickupInfoLocationModel {

    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("countryCode")
    @Expose
    public String countryCode;
    @SerializedName("postCode")
    @Expose
    public String postCode;
    @SerializedName("latitude")
    @Expose
    public double latitude;
    @SerializedName("longitude")
    @Expose
    public double longitude;
    @SerializedName("zoomLevel")
    @Expose
    public int zoomLevel;
    @SerializedName("wholeAddress")
    @Expose
    public String wholeAddress;
}