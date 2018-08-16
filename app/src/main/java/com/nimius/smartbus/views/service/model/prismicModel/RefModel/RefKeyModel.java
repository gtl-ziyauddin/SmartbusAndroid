package com.nimius.smartbus.views.service.model.prismicModel.RefModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RefKeyModel {

    @SerializedName("refs")
    @Expose
    public List<RefModel> refs = new ArrayList<>();
    @SerializedName("oauth_initiate")
    @Expose
    public String oauthInitiate;
    @SerializedName("oauth_token")
    @Expose
    public String oauthToken;
    @SerializedName("version")
    @Expose
    public String version;
    @SerializedName("license")
    @Expose
    public String license;
}