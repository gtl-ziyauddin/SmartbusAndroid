package com.nimius.smartbus.views.service.model.prismicModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ProjectModel {
    @SerializedName("page")
    @Expose
    public int page;
    @SerializedName("results_per_page")
    @Expose
    public int resultsPerPage;
    @SerializedName("results_size")
    @Expose
    public int resultsSize;
    @SerializedName("total_results_size")
    @Expose
    public int totalResultsSize;
    @SerializedName("total_pages")
    @Expose
    public int totalPages;
    @SerializedName("next_page")
    @Expose
    public int nextPage;
    @SerializedName("prev_page")
    @Expose
    public int prevPage;
    @SerializedName("results")
    @Expose
    public List<ResultModel> results = new ArrayList<>();
    @SerializedName("version")
    @Expose
    public String version;
    @SerializedName("license")
    @Expose
    public String license;

    public String name;

    public ProjectModel() {
    }

    public ProjectModel(String name) {
        this.name = name;
    }
}
