package com.nimius.smartbus.views.service.model.prismicModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TitleModel {

    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("text")
    @Expose
    public String text;

}