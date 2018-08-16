package com.nimius.smartbus.views.service.model.PickupInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PickupInfoModel {

    @SerializedName("id")
    @Expose
    public int id;
    //    @SerializedName("location")
//    @Expose
//    public Location location;
    @SerializedName("json")
    @Expose
    public PickupInfoJsonModel infoJsonModel;
    @SerializedName("vendor_id")
    @Expose
    public int vendorId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("ordering")
    @Expose
    public int ordering;


}
