package com.nimius.smartbus.views.service.model.bookingModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusPickupModel {

//    @SerializedName("data")
//    @Expose
//    public ArrayList <List<BusPickupDetailModel>> busPickupList = new ArrayList<>();

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("parent")
    @Expose
    public double parent;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("location")
    @Expose
    public LocationModel locationModel;
    @SerializedName("description")
    @Expose
    public String description;

}
