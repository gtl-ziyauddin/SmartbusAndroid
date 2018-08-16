package com.nimius.smartbus.views.ui.activities;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.zxing.common.StringUtils;
import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.ActivitySplashBinding;
import com.nimius.smartbus.views.database.BookingIdModel;
import com.nimius.smartbus.views.database.BookingViewModel;
import com.nimius.smartbus.views.utility.Constants;

import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends BaseActivity {
    ActivitySplashBinding splashBinding;
    private BookingViewModel bookingViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_splash);
        bookingViewModel = ViewModelProviders.of(this).get(BookingViewModel.class);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                calledAppropriateIntent();
                Animation animate = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate);


                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.e("@@@@@@@@", "onAnimationEnd: ");

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }
        }, 1000);


    }


    @Override
    protected void initVariable() {
        splashBinding = getBinding();
        /**
         * CHANGE NOTIFICATION STATUS BAR COLOR
         */
//        Utils.statusBarBlue(this);


    }

    @Override
    protected void loadData() {

    }


    private void calledAppropriateIntent() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

//    private void getBookingList() {
//        LiveData<List<BookingIdModel>> allBookings = bookingViewModel.getAllBookingVM();
//        observerBookingListResults(allBookings);
//    }
//
//    private void observerBookingListResults(LiveData<List<BookingIdModel>> personsLive) {
//        //observer LiveData
//        personsLive.observe(this, new Observer<List<BookingIdModel>>() {
//            @Override
//            public void onChanged(@Nullable List<BookingIdModel> list) {
//                if (list == null) {
//                    return;
//                }
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                intent.putParcelableArrayListExtra(Constants.EXTRA_BOOKING_DETAIL, (ArrayList<? extends Parcelable>) list);
//                startActivity(intent);
//                finish();
//
//
//            }
//        });
//    }


}
