package com.nimius.smartbus.views.ui.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.ActivityMenuDetailBinding;
import com.nimius.smartbus.views.firebase.FirebaseConstant;
import com.nimius.smartbus.views.service.model.prismicModel.MainContentModel;
import com.nimius.smartbus.views.service.model.prismicModel.ProjectModel;
import com.nimius.smartbus.views.service.repository.ApiConstant;
import com.nimius.smartbus.views.service.retrofit.RestClient;
import com.nimius.smartbus.views.utility.AppPreferences;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.Utils;

import java.io.IOException;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UnreadConversationCountListener;
import io.intercom.android.sdk.identity.Registration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MenuDetailActivity extends BaseActivity {
    ActivityMenuDetailBinding menuDetailBinding;
    String prismicDocumentId;
    AppPreferences appPreferences;

    //----------------------------------------------------------------------------------------------
    // Make sure you go to SampleApplication.java to set your app ID and API key
    //----------------------------------------------------------------------------------------------
    private String USER_ID = "";

    private boolean isContactUs;
    //----------------------------------------------------------------------------------------------
    // If you use Identity Verification you will need to include HMAC
    // We suggest taking these values from your app. You may need to change USER_ID above to match your HMAC
    //----------------------------------------------------------------------------------------------
    private static final String YOUR_HMAC = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_menu_detail);


    }


    @Override
    protected void initVariable() {
        appPreferences = new AppPreferences(this);
        USER_ID = appPreferences.getDeviceKey();
        menuDetailBinding = getBinding();
        setToolbar(menuDetailBinding.layoutToolbar.toolbar, true);
        setNavigationHomeAsUp();
        setTitle("");
//        setTitle(getString(R.string.str_reykjavik_terminal), contactUsBinding.layoutToolbar.toolbarLabel);
        isContactUs = getIntent().getBooleanExtra(Constants.EXTRA_IS_CONTACT_US, false);

        if (isContactUs) {
            menuDetailBinding.layoutChat.setVisibility(View.VISIBLE);
            initIntercom();
        } else {
            menuDetailBinding.layoutChat.setVisibility(View.GONE);
        }

    }


    private void initIntercom() {
        if (TextUtils.isEmpty(MyApplication.YOUR_APP_ID)
                || TextUtils.isEmpty(MyApplication.YOUR_API_KEY)) {
            findViewById(R.id.not_initialized).setVisibility(View.VISIBLE);
        } else {
            //If you have provided a HMAC and data try begin secure session
            if (!TextUtils.isEmpty(YOUR_HMAC)) {
                Intercom.client().setUserHash(YOUR_HMAC);
            }

            //Enable default launcher
//            Intercom.client().setLauncherVisibility(Intercom.Visibility.VISIBLE);
            //Register a user with Intercom
            Intercom.client().registerIdentifiedUser(Registration.create().withUserId(USER_ID));
            //Custom launcher
            FloatingActionButton messengerButton = findViewById(R.id.messenger_button);

            messengerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intercom.client().displayConversationsList();
                }
            });

            //Set the unread count
            int unreadCount = Intercom.client().getUnreadConversationCount();
            menuDetailBinding.unreadCounter.setText(String.valueOf(unreadCount));
            setBadgeVisibility(unreadCount, menuDetailBinding.unreadCounter);
        }
    }


    @Override
    protected void loadData() {
        prismicDocumentId = getIntent().getStringExtra(Constants.EXTRA_PRISMIC_DOC_ID);

        if (prismicDocumentId.equalsIgnoreCase(ApiConstant.documentId_terminal)) {
            ///terminal screen
            /***
             * FIREBASE EVENT
             */
            firebaseAnalytics("", FirebaseConstant.SCREEN_TERMINAL, FirebaseConstant.LABEL_SCREEN_TERMINAL);
        } else if (prismicDocumentId.equalsIgnoreCase(ApiConstant.documentId_contact_us)) {
            ///contact support screen
            /***
             * FIREBASE EVENT
             */
            firebaseAnalytics("", FirebaseConstant.SCREEN_CONTACT_SUPPORT, FirebaseConstant.LABEL_CONTACT_SUPPORT);
        } else if (prismicDocumentId.equalsIgnoreCase(ApiConstant.documentId_missed_pickup)) {
            ///Missed your pickup screen
            /***
             * FIREBASE EVENT
             */
            firebaseAnalytics("", FirebaseConstant.SCREEN_MISSED_PICKUP, FirebaseConstant.LABEL_MISSED_PICKUP);
        }
        getPrismicRefRequest(ApiConstant.prismic_ref_key);


    }


    @Override
    public void onPrismicRefKeySuccess(String refKey) {
        showWaitProgressbar(true);
        getTerminalPageRequest(refKey, ApiConstant.access_token_value, prismicDocumentId);
    }


    private void getTerminalPageRequest(String referenceValue, String accessToken, String query) {
        if (Utils.isNetworkAvailable(MenuDetailActivity.this)) {
            Call<ProjectModel> call = RestClient.getInstance().getApiInterface().getContactUsPage(referenceValue, accessToken, query);
            call.enqueue(new Callback<ProjectModel>() {
                @Override
                public void onResponse(@NonNull Call<ProjectModel> call, @NonNull Response<ProjectModel> response) {
                    Log.e(TAG, "onResponse: ");

                    hideWaitProgressbar();
                    if (response.isSuccessful()) {
                        ProjectModel result = response.body();
                        if (result != null && result.results.size() > 0) {
//                            contactUsBinding.tvTitle.setText(result.results.get(0).data.title.get(0).text);
                            setTitle(result.results.get(0).data.title.get(0).text, menuDetailBinding.layoutToolbar.toolbarLabel);


                            if (result.results.get(0).data.mainContentList.size() > 0) {
                                StringBuilder sb = new StringBuilder();
                                for (MainContentModel mainContent : result.results.get(0).data.mainContentList) {
                                    sb.append(mainContent.text);
                                    sb.append("\n");
                                }
                                menuDetailBinding.tvDetail.setText(sb.toString());
                            }

                            if (result.results.get(0).data.mainImage != null) {
                                menuDetailBinding.ivContactUs.setVisibility(View.VISIBLE);
                                menuDetailBinding.ivContactUs.setImageURI(result.results.get(0).data.mainImage.url);
                            } else {
                                menuDetailBinding.ivContactUs.setVisibility(View.GONE);
                            }

                        }

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(MenuDetailActivity.this, getString(R.string.msg_server_error_try_again));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProjectModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showSnackBar(MenuDetailActivity.this, getString(R.string.msg_server_error_try_again));
                    hideWaitProgressbar();


                }
            });
        } else {
            Log.e(TAG, "no_internet");
            hideWaitProgressbar();


        }
    }


    private final UnreadConversationCountListener unreadConversationCountListener = new UnreadConversationCountListener() {
        @Override
        public void onCountUpdate(int unreadCount) {
            setBadgeVisibility(unreadCount, menuDetailBinding.unreadCounter);
            menuDetailBinding.unreadCounter.setText(String.valueOf(unreadCount));
        }
    };

    private void setBadgeVisibility(int unreadCount, TextView unreadCountView) {
        int visibility = unreadCount == 0 ? View.INVISIBLE : View.VISIBLE;
        unreadCountView.setVisibility(visibility);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isContactUs)
            Intercom.client().removeUnreadConversationCountListener(unreadConversationCountListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isContactUs) {
            Intercom.client().addUnreadConversationCountListener(unreadConversationCountListener);
            Intercom.client().handlePushMessage();
        }
    }


}
