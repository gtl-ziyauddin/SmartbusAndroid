package com.nimius.smartbus.views.service.model.prismicModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataModel {

    @SerializedName("title")
    @Expose
    public List<TitleModel> title = new ArrayList<>();
    @SerializedName("main_content")
    @Expose
    public List<MainContentModel> mainContentList = new ArrayList<>();
    @SerializedName("main_image")
    @Expose
    public MainImageModel mainImage;
}