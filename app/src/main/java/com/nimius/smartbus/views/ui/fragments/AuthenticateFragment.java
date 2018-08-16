package com.nimius.smartbus.views.ui.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.FragmentAuthenticateBinding;
import com.nimius.smartbus.views.callback.ReplaceFragmentCallback;
import com.nimius.smartbus.views.database.BookingIdModel;
import com.nimius.smartbus.views.database.BookingViewModel;
import com.nimius.smartbus.views.firebase.FirebaseConstant;
import com.nimius.smartbus.views.service.model.bookingModel.BookingModel;
import com.nimius.smartbus.views.service.repository.ApiConstant;
import com.nimius.smartbus.views.utility.AppPreferences;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AuthenticateFragment extends AuthenticateApiFragment {
    FragmentAuthenticateBinding authenticateBinding;
    ReplaceFragmentCallback replaceFragmentCallback;
    AppPreferences appPreferences;
    Context mContext;
    private BookingViewModel bookingViewModel;
    private boolean isNewBooking;
    private String customerName;
    private String bookingNumber;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        replaceFragmentCallback = (ReplaceFragmentCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setView(R.layout.fragment_authenticate, true);

    }


    /**
     * INNIT VARIABLES
     */
    @Override
    public void initVariable() {
        authenticateBinding = getBinding();
        authenticateBinding.setAuthenticateFragment(this);
        appPreferences = new AppPreferences(mContext);
        bookingViewModel = ViewModelProviders.of(this).get(BookingViewModel.class);

    }

    @Override
    public void loadData() {
        locationPermission();
        /**IF CUSTOMER NAME IS ALREADY EXIST THEN PRE-FILLED THE
         * CUSTOMER NAME FILED AND DISABLE EDITING**/


        Bundle bundle = getArguments();
        if (bundle != null) {
            /**when user come from add another pickup*/
            isNewBooking = bundle.getBoolean(Constants.EXTRA_NEW_BOOKING);

            /**when user come from deep linking*/
            customerName = bundle.getString(Constants.EXTRA_CUSTOMER_NAME);
            bookingNumber = bundle.getString(Constants.EXTRA_BOOKING_NUMBER);

            if (!TextUtils.isEmpty(bookingNumber)) {
                authenticateBinding.etLastName.setText(customerName);
                authenticateBinding.etBookingNumber.setText(bookingNumber);
                showWaitProgressbar(true);
                authenticateRequest(ApiConstant.query_booking + bookingNumber);
                appPreferences.setCurrentPagePosition(-1);
                isNewBooking = true;
            } else {
                new getLastNameAsyn().execute("");
            }

        } else {
            new getLastNameAsyn().execute("");
        }


        /***
         * FIREBASE EVENT
         */
        firebaseAnalytics("", FirebaseConstant.SCREEN_AUTHENTICATION, FirebaseConstant.LABEL_AUTHENTICATION_USER);

    }


    @Override
    public void permissionDenied(String message) {
        showSnackBar(mContext, message);
    }

    @Override
    public void permissionsPermanentlyDeclined(String message) {
        showPermissionDialog(message, mContext);
    }

    /**
     * CLICK EVENT LISTENER
     *
     * @param view
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_find_my_bus:
                if (TextUtils.isEmpty(authenticateBinding.etLastName.getText().toString())) {
                    authenticateBinding.etLastName.setError(getString(R.string.msg_please_enter_last_name));
                    return;
                }

                if (TextUtils.isEmpty(authenticateBinding.etBookingNumber.getText().toString())) {
                    authenticateBinding.etBookingNumber.setError(getString(R.string.msg_please_enter_booking_number));
                    return;
                }

                hideSoftKeyboard(getActivity());
                showWaitProgressbar(true);
                authenticateRequest(ApiConstant.query_booking + authenticateBinding.etBookingNumber.getText().toString());
                appPreferences.setCurrentPagePosition(-1);
                break;
            case R.id.tv_send_magic_link:
                if (TextUtils.isEmpty(authenticateBinding.etEmailAddress.getText().toString().trim())) {
                    authenticateBinding.etEmailAddress.setError(getString(R.string.msg_please_enter_email_address));
                    return;
                }

                String input = authenticateBinding.etEmailAddress.getText().toString();
                input = input.replace(" ", "");

//                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
//                    authenticateBinding.etEmailAddress.setError(getString(R.string.msg_please_enter_correct_email));
//                    return;
//                }

                if (!emailValidator(input)) {
                    authenticateBinding.etEmailAddress.setError(getString(R.string.msg_please_enter_correct_email));
                    return;
                }
                showSnackBar(getActivity(), getString(R.string.msg_mail_sent));
                hideSoftKeyboard(getActivity());


//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                        "mailto", "gtl.teamtwo@gmail.com", null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "https://www.smartbus.is/where-is-mybus?booking=2323&lname=Smith");
//                startActivity(Intent.createChooser(emailIntent, "Send email..."));
//
//                authenticateBinding.etEmailAddress.setText("");
                break;

        }
    }


    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @Override
    public void onAuthenticateSuccess(BookingModel model) {
        hideWaitProgressbar();
        if (model != null) {
            /**IF ENTER CUSTOMER NAME IS VARY FROM RESPONSE ONE THEN SHOW ALERT TO USER THAT CUSTOMER NAME
             * IS NOT MATCH PLEASE CHECK THE VOUCHER**/

            if (TextUtils.isEmpty(model.smartTour)) {
                ErrorMessageDialog(getString(R.string.app_name), getString(R.string.msg_no_booking_found));
            } else {
                if (authenticateBinding.etLastName.getText().toString().trim().equalsIgnoreCase(model.customerLastName)) {
                    /***
                     * INSERT NEW ENTRY INTO DATABASE
                     */
                    if (Utils.isBookingDepartedDateTime(model.startDateTime)) {
                        ErrorMessageDialog(getString(R.string.app_name), getString(R.string.str_departed));
                        return;
                    }
                    storeBookingIds(bookingDetail(model));
                } else {
                    ErrorMessageDialog(getString(R.string.app_name), getString(R.string.msg_no_customer_name_found_message));
                }
            }

        } else {
            ErrorMessageDialog(getString(R.string.msg_no_pickup_found), getString(R.string.msg_no_pickup_found_message));
        }
    }

    @Override
    public void onFailed(String message) {
        Log.e(TAG, "onFailed: " + message);
        hideWaitProgressbar();
        ErrorMessageDialog(getString(R.string.app_name), message);
    }


    /**
     * CREATE BOOKING DETAIL OBJECT
     *
     * @return
     */
    private BookingIdModel bookingDetail(BookingModel model) {
        return new BookingIdModel(String.valueOf(model.id), model.customerLastName, model.customerName, model.customerEmail, model.startDateTime);

    }


    /***
     * INSERT NEW BOOKING ID INTO DATABASE
     * @param bookingIdModel
     */
    private void storeBookingIds(BookingIdModel bookingIdModel) {
        new isBookingExists(bookingIdModel).execute("");
    }


    /**
     * CHECK WHETHERE BOOKING id IS EXISTS INTO DATABASE OR NOT.
     */
    private class isBookingExists extends AsyncTask<String, Void, Boolean> {
        BookingIdModel bookingIdModel;
        private int exitingPosition;

        private isBookingExists(BookingIdModel model) {
            bookingIdModel = model;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            exitingPosition = bookingViewModel.rowPosition(authenticateBinding.etBookingNumber.getText().toString());
            Log.e(TAG, "doInBackground: row position" + exitingPosition);
            return bookingViewModel.isBookingIdExistsVM(authenticateBinding.etBookingNumber.getText().toString());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            HomeParentFragment homeFragment = new HomeParentFragment();
            Bundle bundle = new Bundle();
            if (result) {
                bundle.putInt(Constants.EXTRA_POSITION, exitingPosition - 1);
            } else {
                bookingViewModel.addBooking(bookingIdModel);
                bundle.putBoolean(Constants.EXTRA_NEW_BOOKING, isNewBooking);
            }
//            else {
//                bookingViewModel.addBooking(bookingIdModel);
//            }
            homeFragment.setArguments(bundle);
            replaceFragmentCallback.changeFragment(homeFragment, "");

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    private class getLastNameAsyn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return bookingViewModel.getLastNameVM();
        }

        @Override
        protected void onPostExecute(String result) {

            authenticateBinding.etLastName.setText(result);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
