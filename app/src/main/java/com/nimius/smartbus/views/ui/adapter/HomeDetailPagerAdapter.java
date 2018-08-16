package com.nimius.smartbus.views.ui.adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nimius.smartbus.views.database.BookingIdModel;
import com.nimius.smartbus.views.ui.fragments.HomeChildFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeDetailPagerAdapter extends FragmentStatePagerAdapter {
    private List<CharSequence> titles = new ArrayList<>();


    private int PAGE_COUNT;
    HomeChildFragment fragment;
    ArrayList<BookingIdModel> bookingList = new ArrayList<>();
    private boolean isNewBooking;


    public HomeDetailPagerAdapter(FragmentManager fm, ArrayList<BookingIdModel> list, boolean booleanValue) {
        super(fm);
        bookingList = list;
        PAGE_COUNT = bookingList.size();
        isNewBooking = booleanValue;


    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        //do nothing here! no call to super.restoreState(arg0, arg1);
    }


    @Override
    public Fragment getItem(int position) {
        fragment = HomeChildFragment.newInstance(isNewBooking, bookingList.get(position), PAGE_COUNT, position);
//        fragment = new HomeChildFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(Constants.EXTRA_BOOKING_DETAIL, bookingList);
//        bundle.putBoolean(Constants.EXTRA_NEW_BOOKING, isNewBooking);
//        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


}