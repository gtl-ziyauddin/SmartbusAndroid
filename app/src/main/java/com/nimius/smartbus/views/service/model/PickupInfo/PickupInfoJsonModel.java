package com.nimius.smartbus.views.service.model.PickupInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PickupInfoJsonModel {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("askForRoomNumber")
    @Expose
    public Boolean askForRoomNumber;
    @SerializedName("location")
    @Expose
    public PickupInfoLocationModel location;
    //    @SerializedName("unLocode")
//    @Expose
//    public UnLocode unLocode;
    @SerializedName("flags")
    @Expose
    public List<String> flags = new ArrayList<>();
}