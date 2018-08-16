package com.nimius.smartbus.views.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nimius.smartbus.R;
import com.nimius.smartbus.views.service.model.prismicModel.RefModel.RefKeyModel;
import com.nimius.smartbus.views.service.retrofit.RestClient;
import com.nimius.smartbus.views.utility.AppPreferences;
import com.nimius.smartbus.views.utility.Utils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class BaseActivity extends AppCompatActivity {


    public static final String TAG = "HPC";
    protected ViewDataBinding binding;
    private Toolbar toolbar;
    ProgressBar progressBar;
    TextView tvNoData;
    LinearLayout layoutProgressbarWait;
    ImageView toolbarIcon;
    TextView toolbarLabel;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public void setTitle(String title, TextView tvTitle) {
        tvTitle.setText(title);
        getSupportActionBar().setTitle("");
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        setSupportActionBar(this.toolbar);

    }

    public void setToolbar(Toolbar toolbar, boolean isLabel) {
        this.toolbar = toolbar;
        setSupportActionBar(this.toolbar);
        if (isLabel) {
            toolbarIcon.setVisibility(View.GONE);
            toolbarLabel.setVisibility(View.VISIBLE);
        } else {
            toolbarIcon.setVisibility(View.VISIBLE);
            toolbarLabel.setVisibility(View.GONE);
        }

    }


    public Toolbar getToolbar() {
        if (toolbar != null) {
            return toolbar;
        }
        return null;
    }

    protected void setView(int layoutResId) {
        binding = DataBindingUtil.setContentView(this, layoutResId);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        layoutProgressbarWait = (LinearLayout) findViewById(R.id.layout_progressbar_wait);
        toolbarIcon = (ImageView) findViewById(R.id.toolbar_icon);
        toolbarLabel = (TextView) findViewById(R.id.toolbar_label);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        initVariable();
        loadData();


    }

    protected abstract void initVariable();

    protected abstract void loadData();

    protected <T extends ViewDataBinding> T getBinding() {
        return (T) binding;
    }


    /**
     * IT IS USED WHERE WE DON'T REQUIRE BACK BUTTON.
     */
    public void setNavigationHomeAsUp() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * IT IS USED WHERE WE DON'T REQUIRE BACK BUTTON.
     */
    public void setNavigationHomeAsUp(boolean isVisible) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isVisible);
            getSupportActionBar().setHomeButtonEnabled(isVisible);
        }
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void hideToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
        }
    }


    /**
     * Method for display snack bar
     *
     * @param context
     * @param message = SHOW CUSTOM MESSAGE
     **/

    public void showSnackBar(Context context, String message) {
        ViewGroup view = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        Snackbar snackbar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);
        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        viewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        View viewTv = snackbar.getView();
        TextView tv = (TextView) viewTv.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        snackbar.show();


    }

    public void showSnackBar(Context context, String message, View view) {
        Snackbar snackbar = Snackbar.make(view, "" + message, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED);
        ViewGroup viewGroup = (ViewGroup) snackbar.getView();
        viewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        View viewTv = snackbar.getView();
        TextView tv = (TextView) viewTv.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
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


    /**
     * ERROR MESSAGE DIALOG
     */
    public void ErrorMessageDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, R.style.MyDialogTheme);
        builder.setTitle(title);
        builder.setMessage(message);
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialogOkCallBack();
                    }
                });


        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }


    /**
     * SHOW PROGRESS BAR
     */
    public void showProgressbar(ProgressBar progressbar) {
        if (progressbar != null) {
            progressbar.setVisibility(View.VISIBLE);
        }

    }

    /**
     * HIDE PROGRESS BAR
     */
    public void hideProgressbar(ProgressBar progressbar) {
        if (progressbar != null) {
            progressbar.setVisibility(View.GONE);
        }
    }


    /**
     * SHOW PROGRESS BAR
     */
    public void showProgressbar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
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


    /**
     * IT WILL CHECK SIZE OF LIST IF SIZE IS ZERO OR LESS THEN DISPLAY THE ERROR MESSAGE.
     *
     * @param size
     * @param message
     */
    public void checkListSizeMessage(int size, String message) {
        if (size <= 0)
            showErrorMessage(message);
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


    /***
     * FUNCTION TO HIDE KEYBOARD
     * @param activity
     */
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public void onPrismicRefKeySuccess(String refKey) {

    }


    /***
     * REQUEST FOR PRISMIC REF KEY
     * @param url
     */
    public void getPrismicRefRequest(String url) {
        if (Utils.isNetworkAvailable(BaseActivity.this)) {
            Call<RefKeyModel> call = RestClient.getInstance().getApiInterface().getPrismicRefApi(url);
            call.enqueue(new Callback<RefKeyModel>() {
                @Override
                public void onResponse(@NonNull Call<RefKeyModel> call, @NonNull Response<RefKeyModel> response) {
                    Log.e(TAG, "onResponse: ");

                    hideWaitProgressbar();
                    if (response.isSuccessful()) {
                        RefKeyModel result = response.body();
                        if (result != null)
                            onPrismicRefKeySuccess(result.refs.get(0).ref);

                    } else {
                        try {
                            if (!TextUtils.isEmpty(response.errorBody().string())) {
                                Log.e(TAG, "onResponse: error=" + response.message());
                                showSnackBar(BaseActivity.this, getString(R.string.msg_server_error_try_again));
                                hideWaitProgressbar();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RefKeyModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    showSnackBar(BaseActivity.this, t.getMessage());
                    hideWaitProgressbar();


                }
            });
        } else {
            Log.e(TAG, "no_internet");
            showSnackBar(BaseActivity.this, getString(R.string.msg_no_internet));
            hideWaitProgressbar();
        }
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