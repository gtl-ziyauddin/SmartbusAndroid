package com.nimius.smartbus.views.service.model.prismicModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResultModel {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("uid")
    @Expose
    public int uid;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("href")
    @Expose
    public String href;
    @SerializedName("first_publication_date")
    @Expose
    public String firstPublicationDate;
    @SerializedName("last_publication_date")
    @Expose
    public String lastPublicationDate;
    @SerializedName("lang")
    @Expose
    public String lang;

    @SerializedName("data")
    @Expose
    public DataModel data;
}