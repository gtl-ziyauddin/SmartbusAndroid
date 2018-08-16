package com.nimius.smartbus.views.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.FragmentHomeParentBinding;
import com.nimius.smartbus.views.firebase.FirebaseConstant;
import com.nimius.smartbus.views.ui.adapter.HomeDetailPagerAdapter;
import com.nimius.smartbus.views.callback.ReplaceFragmentCallback;
import com.nimius.smartbus.views.database.BookingIdModel;
import com.nimius.smartbus.views.database.BookingViewModel;
import com.nimius.smartbus.views.ui.customeView.CustomViewPager;
import com.nimius.smartbus.views.utility.AppPreferences;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.Utils;

import java.util.ArrayList;
import java.util.List;


public class HomeParentFragment extends AuthenticateApiFragment {
    ReplaceFragmentCallback replaceFragmentCallback;
    FragmentHomeParentBinding homeParentBinding;
    HomeDetailPagerAdapter detailPagerAdapter;
    AppPreferences appPreferences;
    private BookingViewModel bookingViewModel;
    private boolean isNewBooking;
    List<BookingIdModel> bookingList = new ArrayList<>();
    private boolean isPermissionDenied;


    /**
     * current pickup list position
     **/
    private int exitingPosition;

    ///temp
    private int mCurrentPosition;
    private int mScrollState;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        replaceFragmentCallback = (ReplaceFragmentCallback) context;
    }


    /***
     *  WHEN LOCATION PERMISSION  IS DENIED
     * @param value
     */
    public void locationPermissionDenied(boolean value) {
        isPermissionDenied = value;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "HomeParentFragment onResume: called");
        if (!isPermissionDenied) {
//            getBookingList();
        }
        isPermissionDenied = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "HomeParentFragment onResume: called");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setView(R.layout.fragment_home_parent, true);

    }


    /**
     * INNIT VARIABLES
     */
    @Override
    public void initVariable() {
        Log.e(TAG, "HomeParentFragment initVariable: called");
        homeParentBinding = getBinding();
        appPreferences = new AppPreferences(mContext);
        bookingViewModel = ViewModelProviders.of(this).get(BookingViewModel.class);
        ///ASK FOR LOCATION PERMISSION
//        locationPermission();

    }

    @Override
    public void loadData() {
        Log.e(TAG, "HomeParentFragment loadData: called");
        Bundle bundle = getArguments();
        if (bundle != null) {
            isNewBooking = bundle.getBoolean(Constants.EXTRA_NEW_BOOKING);
            exitingPosition = bundle.getInt(Constants.EXTRA_POSITION, 0);
        }


        homeParentBinding.pagerHome.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                mCurrentPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                handleScrollState(state);
//                mScrollState = state;

            }
        });

        /***
         * FIREBASE EVENT
         */
        firebaseAnalytics("", FirebaseConstant.SCREEN_WHERE_IS_MY_BUS, FirebaseConstant.LABEL_WHERE_IS_MY_BUS);


//        isPermissionDenied = true;
        getBookingList();


    }

    /***
     * FOR CIRCULAR SLIDER
     * @param state
     */
    private void handleScrollState(final int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setNextItemIfNeeded();
        }
    }

    private void setNextItemIfNeeded() {
        if (!isScrollStateSettling()) {
            handleSetNextItem();
        }
    }

    private boolean isScrollStateSettling() {
        return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
    }

    private void handleSetNextItem() {
        final int lastPosition = homeParentBinding.pagerHome.getAdapter().getCount() - 1;
        if (mCurrentPosition == 0) {
            homeParentBinding.pagerHome.setCurrentItem(lastPosition, false);
        } else if (mCurrentPosition == lastPosition) {
            homeParentBinding.pagerHome.setCurrentItem(0, false);
        }
    }


    public void previousPager(int position) {
        Log.e(TAG, "currentPosition: " + position);

        if (position == 0) {
            homeParentBinding.pagerHome.setCurrentItem(bookingList.size() - 1);
        } else {
            homeParentBinding.pagerHome.setCurrentItem(position - 1);
        }

    }

    public void nextPager(int position) {
        if (position == bookingList.size() - 1) {
            homeParentBinding.pagerHome.setCurrentItem(0);
        } else {
            homeParentBinding.pagerHome.setCurrentItem(position + 1);
        }
    }

    /***
     * SET PAGER INDICATOR BASE ON PAGE COUNT
     */
//    private void setUiPageViewController(int counts) {
//        dotsCount = counts;
//        dots = new ImageView[dotsCount];
//        homeParentBinding.viewPagerCountDots.removeAllViews();
//        for (int i = 0; i < dotsCount; i++) {
//            dots[i] = new ImageView(mContext);
//            dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pager_unselected_dot));
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//
//            params.setMargins(4, 0, 4, 0);
//
//
//            homeParentBinding.viewPagerCountDots.addView(dots[i], params);
//        }
//
//        if (dotsCount > 0) {
//            dots[0].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pager_selected_dot));
//        }
//    }


    /**
     * FETCH STORE LIST OF BOOKING DETAIL
     */
    private void getBookingList() {
        LiveData<List<BookingIdModel>> allBookings = bookingViewModel.getAllBookingVM();
        observerBookingListResults(allBookings);
    }


    /***
     * GET THE RESULT OF getAllBookingVM()
     * @param personsLive
     */
    private void observerBookingListResults(LiveData<List<BookingIdModel>> personsLive) {
        //observer LiveData
        personsLive.observe(this, new Observer<List<BookingIdModel>>() {
            @Override
            public void onChanged(@Nullable List<BookingIdModel> list) {
                if (list == null) {
                    return;
                }
                Log.e(TAG, "onChanged: " + list.size());
                bookingList.clear();
//                bookingList.addAll(list);
//                detailPagerAdapter = new HomeDetailPagerAdapter(getChildFragmentManager(), (ArrayList<BookingIdModel>) list, isNewBooking);
                bookingList.addAll(removeDepartedEntry(list));
                if (bookingList.size() > 0) {
                    detailPagerAdapter = new HomeDetailPagerAdapter(getChildFragmentManager(), (ArrayList<BookingIdModel>) bookingList, isNewBooking);
                    homeParentBinding.pagerHome.setAdapter(detailPagerAdapter);
                    homeParentBinding.pagerHome.setOffscreenPageLimit(0);

                    /**user come back from clicking where is my bus button**/
                    if (appPreferences.getCurrentPagePosition() != -1) {
                        homeParentBinding.pagerHome.setCurrentItem(appPreferences.getCurrentPagePosition());
                        appPreferences.setCurrentPagePosition(-1);
                    } else {
                        /**user enter new booking or same**/
                        if (isNewBooking) {
//                    selectedPos(list.size()-1);
                            homeParentBinding.pagerHome.setCurrentItem(list.size() - 1);
                        } else {
                            homeParentBinding.pagerHome.setCurrentItem(exitingPosition);
                        }
                    }
                } else {
                    AuthenticateFragment authenticateFragment = new AuthenticateFragment();
                    replaceFragmentCallback.changeFragment(authenticateFragment, "");
                }


            }
        });
    }

    private List<BookingIdModel> removeDepartedEntry(List<BookingIdModel> list) {
        List<BookingIdModel> listData = new ArrayList<>();
        for (BookingIdModel model : list) {
            if (Utils.isBookingDepartedDateTime(model.getStartDateTime())) {
                bookingViewModel.deleteBooking(model);
            } else {
                listData.add(model);
            }
        }
        return listData;

    }

}
