package com.nimius.smartbus.views.ui.activities;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.ActivityPickupInfoBinding;
import com.nimius.smartbus.views.firebase.FirebaseConstant;
import com.nimius.smartbus.views.ui.adapter.FilteredArrayAdapter;
import com.nimius.smartbus.views.ui.adapter.PlacesApiArrayAdapter;
import com.nimius.smartbus.views.callback.PopulateListCallback;
import com.nimius.smartbus.views.service.model.PickupInfo.PickupInfoModel;
import com.nimius.smartbus.views.service.model.prismicModel.MainContentModel;
import com.nimius.smartbus.views.service.model.prismicModel.ProjectModel;
import com.nimius.smartbus.views.service.repository.ApiConstant;
import com.nimius.smartbus.views.service.retrofit.PlaceApiClient;
import com.nimius.smartbus.views.service.retrofit.RestClient;
import com.nimius.smartbus.views.utility.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PickUpInfoActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    ActivityPickupInfoBinding pickupInfoBinding;
    List<PickupInfoModel> busPickupList = new ArrayList<>();
    PlacesApiArrayAdapter apiArrayAdapter;
//    FilteredArrayAdapter filteredArrayAdapter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_pickup_info);


    }


    @Override
    protected void initVariable() {
        pickupInfoBinding = getBinding();
        setToolbar(pickupInfoBinding.layoutToolbar.toolbar, true);
        setNavigationHomeAsUp();
        setTitle("");
//        setTitle(getString(R.string.str_pick_up_locaiton), pickupInfoBinding.layoutToolbar.toolbarLabel);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void loadData() {
        showWaitProgressbar(true);
        getPrismicRefRequest(ApiConstant.prismic_ref_key);

        pickupInfoBinding.autoLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemSelected: " + busPickupList.get(i).title);
                hideSoftKeyboard(PickUpInfoActivity.this);
                updateMap(busPickupList.get(i));
            }
        });
        pickupInfoBinding.autoLocation.setOnClickListener(this);

//        pickupInfoBinding.autoLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pickupInfoBinding.autoLocation.showDropDown();
//            }
//        });


        pickupInfoBinding.autoLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
//                apiArrayAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /***
         * FIREBASE EVENT
         */
        firebaseAnalytics("", FirebaseConstant.SCREEN_PICKUP_INFO, FirebaseConstant.LABEL_PICKUP_INFO);

    }


    @Override
    public void onClick(View view) {
        pickupInfoBinding.autoLocation.showDropDown();
    }

    PopulateListCallback populateListCallback = new PopulateListCallback() {
        @Override
        public void populateList() {
//            pickupInfoBinding.autoLocation.setFocusable(true);
//            pickupInfoBinding.autoLocation.setFocusableInTouchMode(true);

        }
    };

    @Override
    public void onPrismicRefKeySuccess(String refKey) {
        showWaitProgressbar(true);
        getPickUpRequest(refKey, ApiConstant.access_token_value, ApiConstant.documentId_pickup);
    }

    private void updateMap(PickupInfoModel model) {
        if (mMap != null) {
            // Add a marker in Sydney and move the camera
            mMap.clear();
            LatLng locationLatLog = new LatLng(model.infoJsonModel.location.latitude, model.infoJsonModel.location.longitude);
            mMap.addMarker(new MarkerOptions().position(locationLatLog).title(model.infoJsonModel.title));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLog));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f));

            CameraPosition newCamPos = new CameraPosition(new LatLng(model.infoJsonModel.location.latitude, model.infoJsonModel.location.longitude),
                    15.5f,
                    mMap.getCameraPosition().tilt, //use old tilt
                    mMap.getCameraPosition().bearing); //use old bearing
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(PickUpInfoActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(PickUpInfoActivity.this);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());
                    info.addView(title);

                    return info;
                }
            });

//            mMap.getUiSettings().setScrollGesturesEnabled(true);
        }
    }


    private void sortPickupInfoList() {
        Collections.sort(busPickupList, new Comparator<PickupInfoModel>() {

            @Override
            public int compare(PickupInfoModel lhs, PickupInfoModel rhs) {
                //here getTitle() method return app name...
                return lhs.infoJsonModel.title.compareTo(rhs.infoJsonModel.title);

            }
        });


        apiArrayAdapter = new PlacesApiArrayAdapter(PickUpInfoActivity.this, R.layout.raw_places_api, busPickupList,populateListCallback);
//        filteredArrayAdapter = new FilteredArrayAdapter(PickUpInfoActivity.this, R.layout.raw_places_api, busPickupList);
        pickupInfoBinding.autoLocation.setAdapter(apiArrayAdapter);
//        pickupInfoBinding.autoLocation.setThreshold(1);
    }


    private void getPlacesRequest(String url) {
        if (Utils.isNetworkAvailable(PickUpInfoActivity.this)) {
            Call<List<PickupInfoModel>> call = PlaceApiClient.getInstance().getApiInterface().getPlacesApi(url);
            call.enqueue(new Callback<List<PickupInfoModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<PickupInfoModel>> call, @NonNull Response<List<PickupInfoModel>> response) {
                    Log.e(TAG, "onResponse: ");

                    hideWaitProgressbar();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            for (PickupInfoModel infoModel : response.body()) {
                                if (infoModel.infoJsonModel.flags.size() > 0)
                                    if (infoModel.infoJsonModel.flags.contains("economy")) {
                                        busPickupList.add(infoModel);
                                    }
                            }
                            Log.e(TAG, "onResponse: " + busPickupList.size());
                            sortPickupInfoList();
                        }

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(PickUpInfoActivity.this, response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<List<PickupInfoModel>> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();


                }
            });
        } else {
            showSnackBar(PickUpInfoActivity.this, getString(R.string.msg_no_internet));
            hideWaitProgressbar();

        }
    }



    private void getPickUpRequest(String referenceValue, String accessToken, String query) {
        if (Utils.isNetworkAvailable(PickUpInfoActivity.this)) {
            Call<ProjectModel> call = RestClient.getInstance().getApiInterface().getContactUsPage(referenceValue, accessToken, query);
            call.enqueue(new Callback<ProjectModel>() {
                @Override
                public void onResponse(@NonNull Call<ProjectModel> call, @NonNull Response<ProjectModel> response) {
                    Log.e(TAG, "onResponse: ");

                    hideWaitProgressbar();
                    if (response.isSuccessful()) {
                        ProjectModel result = response.body();
                        if (result != null && result.results.size() > 0) {

                            if (result.results.get(0).data.title.size() > 0) {
                                setTitle(result.results.get(0).data.title.get(0).text, pickupInfoBinding.layoutToolbar.toolbarLabel);
                            } else {
//                                setTitle(getString(R.string.str_pick_up_locaiton), pickupInfoBinding.layoutToolbar.toolbarLabel);
                            }

                            if (result.results.get(0).data.mainContentList.size() > 0) {
                                StringBuilder sb = new StringBuilder();
                                for (MainContentModel mainContent : result.results.get(0).data.mainContentList) {
                                    sb.append(mainContent.text);
                                    sb.append("\n");
                                }
                                pickupInfoBinding.tvBottomInfo.setText(sb.toString());
                            }

//                            contactUsBinding.ivContactUs.setImageURI(result.results.get(0).data.mainImage.url);

                        }

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(PickUpInfoActivity.this, response.message());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    /**CALL PLACE API ONCE INFO CALLED**/
                    showWaitProgressbar(true);
                    getPlacesRequest(ApiConstant.param_places + ApiConstant.param_format);

                }

                @Override
                public void onFailure(@NonNull Call<ProjectModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showErrorMessage(getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();


                }
            });
        } else {
            Log.e(TAG, "no_internet");
            hideWaitProgressbar();


        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard(PickUpInfoActivity.this);
        super.onBackPressed();
    }


}
