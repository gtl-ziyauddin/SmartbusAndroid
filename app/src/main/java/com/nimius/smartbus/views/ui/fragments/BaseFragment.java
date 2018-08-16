/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nimius.smartbus.views.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nimius.smartbus.R;
import com.nimius.smartbus.views.callback.InitViewsCallback;


public abstract class BaseFragment extends Fragment implements InitViewsCallback {


    public static final String TAG = BaseFragment.class.getSimpleName();
    protected Context mContext;
    ProgressBar progressBar;
    LinearLayout layoutProgressbarWait;
    TextView tvNoData;
    protected ViewDataBinding binding;
    private int layoutId;
    private boolean hasOptionMenu = false;
    Toolbar toolbar;
    AppCompatActivity activity;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;


    }

    protected void setView(int layoutId) {
        this.layoutId = layoutId;
        hasOptionMenu = false;
    }

    protected void setView(int layoutId, boolean hasOptionMenu) {
        this.layoutId = layoutId;
        this.hasOptionMenu = hasOptionMenu;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);
        setHasOptionsMenu(hasOptionMenu);
        activity = (AppCompatActivity) getActivity();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        layoutProgressbarWait = (LinearLayout) view.findViewById(R.id.layout_progressbar_wait);
    }

    protected <T extends ViewDataBinding> T getBinding() {
        return (T) binding;
    }


    public void setData() {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setData();
        initVariable();
        loadData();
    }


    public void setTitle(String title, TextView tvTitle) {
        tvTitle.setText(title);
        activity.getSupportActionBar().setTitle("");
    }

    public void setTitle(String title) {
        activity.getSupportActionBar().setTitle(title);
    }


    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        activity.setSupportActionBar(this.toolbar);
    }


    public Toolbar getToolbar() {
        if (toolbar != null) {
            return toolbar;
        }
        return null;
    }


    /**
     * IT IS USED WHERE WE DON'T REQUIRE BACK BUTTON.
     */
    public void setNavigationHomeAsUp() {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
        }

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void onBackPressed() {
        activity.onBackPressed();

    }


    /**
     * IT IS USED WHERE WE DON'T REQUIRE BACK BUTTON.
     */
    public void setNavigationHomeAsUp(boolean isVisible) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(isVisible);
            activity.getSupportActionBar().setHomeButtonEnabled(isVisible);
        }
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }


    public void showSnackBar(Context context, String message) {
        ViewGroup view = (ViewGroup) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);
        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        viewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        View viewTv = snackbar.getView();
        TextView tv = (TextView) viewTv.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        viewTv.setElevation(8);
        snackbar.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                //see Snackbar.Callback docs for event details
                Log.e(TAG, "onDismissed: ");

            }

            @Override
            public void onShown(Snackbar snackbar) {
                Log.e(TAG, "onShown: ");
            }
        });
        snackbar.show();
    }


    /***
     * callback methods
     */
    public void dialogOkCallBack() {

    }

    public void dialogCancelCallBack() {

    }


    /**
     * DATA LOG DIALOG
     */
    public void OkayCancelDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(message);
        String positiveText = getString(android.R.string.ok);
        String negativeText = getString(android.R.string.cancel);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialogOkCallBack();
                    }
                });
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialogCancelCallBack();
                    }
                });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public void dialogOnlyOkayCallBack() {

    }

    /**
     * DATA LOG DIALOG
     */
    public void ErrorMessageDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle(title);
        builder.setMessage(message);
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialogOnlyOkayCallBack();

                    }
                });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


    /**
     * SHOW CUSTOM MESSSAGE
     *
     * @param message
     */
    public void showErrorMessage(String message) {

        if (tvNoData != null) {
            tvNoData.setVisibility(View.VISIBLE);
            tvNoData.setText(message);
        }
    }

    /**
     * HIDE ERROR  MESSAGE
     */
    public void hideErrorMessage() {
        if (tvNoData != null) {
            tvNoData.setVisibility(View.GONE);
        }

    }


    /**
     * SHOW PROGRESS BAR
     */
    public void showProgressbar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    /**
     * HIDE PROGRESS BAR
     */
    public void hideProgressbar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    /**
     * DIALOG PROGRESS WITH CANCELABLE
     *
     * @param isCancelable
     */
    public void showWaitProgressbar(boolean isCancelable) {
        if (layoutProgressbarWait != null) {
            layoutProgressbarWait.setVisibility(View.VISIBLE);
            layoutProgressbarWait.bringToFront();
            if (isCancelable) {
                layoutProgressbarWait.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutProgressbarWait.setVisibility(View.GONE);
                    }
                });
            }

        }

    }

    /**
     * HIDE PROGRESS BAR
     */
    public void hideWaitProgressbar() {
        if (layoutProgressbarWait != null) {
            layoutProgressbarWait.setVisibility(View.GONE);
        }
    }


    /***
     * THIS METHOD WOULD BE OVERRIDE BY OTHERS CLASSES WHEREVER IS REQUIRED
     * @param actionType
     * @param message
     * @param isSuccess
     */
    public void onApiResponse(int actionType, int count, String message, boolean isSuccess) {

    }


    /***
     * HIDE VIRTUAL KEYBOARD
     * @param activity
     */
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    /***
     * PERMISSION ERROR DIALOG
     * @param message
     */
    public void showPermissionDialog(String message, final Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        builder.setTitle(mContext.getString(R.string.app_name));
        builder.setMessage(message);
        builder.setPositiveButton(mContext.getString(R.string.str_settings),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        appDetailIntent(mContext);
                    }
                });

        builder.setNegativeButton(mContext.getString(R.string.str_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    /**
     * IT WILL OPEN APP DETAIL SCREEN
     **/
    public void appDetailIntent(Context mContext) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }


    public void firebaseAnalytics(String id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

    }


}
