package com.nimius.smartbus.views.service.model.prismicModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainImageModel {

    @SerializedName("dimensions")
    @Expose
    public DimensionsModel dimensions;
    @SerializedName("alt")
    @Expose
    public String alt;
    @SerializedName("copyright")
    @Expose
    public String copyright;
    @SerializedName("url")
    @Expose
    public String url;
}