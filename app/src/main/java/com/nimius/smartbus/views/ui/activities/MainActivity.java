package com.nimius.smartbus.views.ui.activities;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.ActivityMainBinding;
import com.nimius.smartbus.views.callback.ButtonClickCallback;
import com.nimius.smartbus.views.callback.LocationDeniedCallback;
import com.nimius.smartbus.views.callback.PopulateListCallback;
import com.nimius.smartbus.views.callback.ReplaceFragmentCallback;
import com.nimius.smartbus.views.database.BookingIdModel;
import com.nimius.smartbus.views.database.BookingViewModel;
import com.nimius.smartbus.views.ui.fragments.AuthenticateFragment;
import com.nimius.smartbus.views.ui.fragments.CurrentLocationBaseFragment;
import com.nimius.smartbus.views.ui.fragments.HomeParentFragment;
import com.nimius.smartbus.views.ui.fragments.MenuDialogFragment;
import com.nimius.smartbus.views.ui.fragments.ToursActivitiesFragment;
import com.nimius.smartbus.views.utility.AppPreferences;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.EasyPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class MainActivity extends BaseActivity implements ReplaceFragmentCallback,
        PopulateListCallback, ButtonClickCallback, LocationDeniedCallback {
    ActivityMainBinding mainBinding;
    private Fragment currentFragment;
    private Fragment previousFragment;
    private Stack<Fragment> stack = new Stack<>();
    AppPreferences appPreferences;
    MenuDialogFragment addPhotoBottomDialogFragment;
    private BookingViewModel bookingViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_main);
    }


    @Override
    protected void initVariable() {
        mainBinding = getBinding();
        mainBinding.setMainActivity(this);
        appPreferences = new AppPreferences(this);
        setToolbar(mainBinding.includeToolbar.toolbar);
        bookingViewModel = ViewModelProviders.of(this).get(BookingViewModel.class);

    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String url = intent.getData().toString();
            if (!TextUtils.isEmpty(url)) {
                Log.e(TAG, "onCreate: deep linking=" + url);

                String[] bookingNumber = null;
                String name = url.substring(url.lastIndexOf("=") + 1);
                Log.e(TAG, "onCreate: name" + name);
                String[] parts = url.split("booking=");
                if (parts.length > 0) {
                    Log.e(TAG, "onCreate: parts" + parts[1]);
                    bookingNumber = parts[1].split("&");
                    Log.e(TAG, "onCreate: booking number" + bookingNumber[0]);
                }

                if (bookingNumber != null) {
                    AuthenticateFragment authenticateFragment = new AuthenticateFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EXTRA_CUSTOMER_NAME, name);
                    bundle.putString(Constants.EXTRA_BOOKING_NUMBER, bookingNumber[0]);
                    authenticateFragment.setArguments(bundle);
                    pushFragments(authenticateFragment);
                    setTitle("");
                } else { ///in rare case
                    getBookingList();
                }
            } else { ///in rare case
                getBookingList();
            }

        } else {
            getBookingList();
        }
    }


    private void callFragments(List<BookingIdModel> bookingList) {
        if (bookingList != null && bookingList.size() > 0) {
            HomeParentFragment homeParentFragment = new HomeParentFragment();
            pushFragments(homeParentFragment);

        } else {
            AuthenticateFragment authenticateFragment = new AuthenticateFragment();
            pushFragments(authenticateFragment);
        }
        setTitle("");
    }

    /**
     * CLICK EVENT LISTENER
     *
     * @param view
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_where_bus:
                if (currentFragment instanceof HomeParentFragment) {
                    return;
                } else {

                    new getLastNameAsyn().execute("");
//                    if (TextUtils.isEmpty(appPreferences.getCustomerName())) {
//                        AuthenticateFragment authenticateFragment = new AuthenticateFragment();
//                        pushFragments(authenticateFragment);
//                    } else {
//                        HomeParentFragment homeParentFragment = new HomeParentFragment();
//                        pushFragments(homeParentFragment);
//                    }
//
//                    setTitle("");
//                    whereIsMyBusSelected();
                }

                break;
            case R.id.tv_tours_activities:
                if (currentFragment instanceof ToursActivitiesFragment) {
                    return;
                }
//                showSnackBar(MainActivity.this, "Open tour and activities page");
                ToursActivitiesFragment toursActivitiesFragment = new ToursActivitiesFragment();
                pushFragments(toursActivitiesFragment);
                setTitle("");
                tourActivitiesSelected();
                break;
        }
    }


    /***
     * FETCH BOOKING LIST FROM LOCAL DATABASE
     */
    private void getBookingList() {
        LiveData<List<BookingIdModel>> allBookings = bookingViewModel.getAllBookingVM();
        observerBookingListResults(allBookings);
    }


    private void observerBookingListResults(LiveData<List<BookingIdModel>> personsLive) {
        //observer LiveData
        personsLive.observe(this, new Observer<List<BookingIdModel>>() {
            @Override
            public void onChanged(@Nullable List<BookingIdModel> list) {
                if (list == null) {
                    return;
                }
                callFragments(list);
            }
        });
    }


    private class getLastNameAsyn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return bookingViewModel.getLastNameVM();
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result)) {
                AuthenticateFragment authenticateFragment = new AuthenticateFragment();
                pushFragments(authenticateFragment);
            } else {
                HomeParentFragment homeParentFragment = new HomeParentFragment();
                pushFragments(homeParentFragment);
            }

            setTitle("");
            whereIsMyBusSelected();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: requestCode" + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (currentFragment instanceof AuthenticateFragment) {
                    ((AuthenticateFragment) currentFragment).calledStartLocationUpdate();
                }

                if (currentFragment instanceof HomeParentFragment) {
                    ((HomeParentFragment) currentFragment).calledStartLocationUpdate();
                }


            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_option:
                addPhotoBottomDialogFragment =
                        MenuDialogFragment.newInstance();
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                        "add_photo_dialog_fragment");


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void populateList() {
        if (addPhotoBottomDialogFragment != null)
            addPhotoBottomDialogFragment.dismiss();


    }

    /***
     * WHEN LOCATION PERMISSION  IS DENIED
     * @param value
     */

    @Override
    public void permissionDenied(boolean value) {
        if (currentFragment instanceof HomeParentFragment) {
            ((HomeParentFragment) currentFragment).locationPermissionDenied(value);

        }
    }


    private void tourActivitiesSelected() {
        mainBinding.tvToursActivities.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color_trans_15));
        mainBinding.tvToursActivities.setEnabled(false);
        mainBinding.tvWhereBus.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color_trans_10));
        mainBinding.tvWhereBus.setEnabled(true);
    }


    private void whereIsMyBusSelected() {
        mainBinding.tvToursActivities.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color_trans_10));
        mainBinding.tvToursActivities.setEnabled(true);
        mainBinding.tvWhereBus.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color_trans_15));
        mainBinding.tvWhereBus.setEnabled(false);
    }

    @Override
    public void changeFragment(Fragment fragment, String title) {
        if (title.equalsIgnoreCase("Pickup")) {

        }
        pushFragments(fragment);
        setTitle(title);
    }


    public void replaceFragment(Fragment fragment) {
        this.currentFragment = fragment;
        // Create new fragment and transaction
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
//        transaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_out_left);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed

//        if (currentFragment instanceof AuthenticateFragment) {
////            mainBinding.tvWhereBus.setText(getString(R.string.str_book_now));
//            mainBinding.tvWhereBus.setVisibility(View.GONE);
//        } else {
////            mainBinding.tvWhereBus.setText(getString(R.string.str_where_my_bus));
//            mainBinding.tvWhereBus.setVisibility(View.VISIBLE);
//        }


        transaction.replace(R.id.container, fragment);
        // Commit the transaction
        transaction.commit();
//        invalidateOptionsMenu();

        if (currentFragment instanceof HomeParentFragment) {
            whereIsMyBusSelected();
        }

        if (currentFragment instanceof AuthenticateFragment) {
            mainBinding.tvWhereBus.setEnabled(true);
        }
    }


    public void pushFragments(Fragment frag) {
        stack.push(frag);
        replaceFragment(frag);
    }


    public void popFragments() {
        /*
         * Select the second last fragment in current tab's stack.. which will
         * be shown after the fragment transaction given below
         */
        Fragment frag = stack.elementAt(stack.size() - 2);
        // / pop current fragment from stack.. /
        stack.pop();
        /*
         * We have the target fragment in hand.. Just show it.. Show a standard
         * navigation_main animation
         */
        replaceFragment(frag);
    }


    @Override
    public void onBackPressed() {
        if (currentFragment != null && currentFragment instanceof HomeParentFragment
                || currentFragment instanceof AuthenticateFragment) {

            super.onBackPressed();
        } else {
            if (stack.size() > 0) {
                previousFragment = currentFragment;
                popFragments();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void previousPosition(int position) {
        Log.e(TAG, "previousPosition: " + position);
        if (currentFragment instanceof HomeParentFragment) {
            ((HomeParentFragment) currentFragment).previousPager(position);
        }
    }

    @Override
    public void nextPosition(int position) {
        Log.e(TAG, "previousPosition: " + position);
        if (currentFragment instanceof HomeParentFragment) {
            ((HomeParentFragment) currentFragment).nextPager(position);
        }
    }
}
