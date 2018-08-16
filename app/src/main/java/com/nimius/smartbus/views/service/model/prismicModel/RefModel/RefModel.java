package com.nimius.smartbus.views.service.model.prismicModel.RefModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefModel {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("ref")
    @Expose
    public String ref;
    @SerializedName("label")
    @Expose
    public String label;
    @SerializedName("isMasterRef")
    @Expose
    public boolean isMasterRef;
}