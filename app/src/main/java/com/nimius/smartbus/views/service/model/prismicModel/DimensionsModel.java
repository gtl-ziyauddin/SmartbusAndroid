package com.nimius.smartbus.views.service.model.prismicModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DimensionsModel {

    @SerializedName("width")
    @Expose
    public int width;
    @SerializedName("height")
    @Expose
    public int height;
}