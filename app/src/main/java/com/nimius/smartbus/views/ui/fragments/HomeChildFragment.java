package com.nimius.smartbus.views.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.WriterException;
import com.nimius.smartbus.R;
import com.nimius.smartbus.databinding.FragmentHomeChildBinding;
import com.nimius.smartbus.views.callback.ButtonClickCallback;
import com.nimius.smartbus.views.callback.LocationDeniedCallback;
import com.nimius.smartbus.views.callback.ReplaceFragmentCallback;
import com.nimius.smartbus.views.database.BookingIdModel;
import com.nimius.smartbus.views.service.model.bookingModel.BookingModel;
import com.nimius.smartbus.views.service.model.bookingTourModel.BookingTourModel;
import com.nimius.smartbus.views.service.model.carDetailModel.CarDetailModel;
import com.nimius.smartbus.views.service.repository.ApiConstant;
import com.nimius.smartbus.views.service.repository.BookingIntentService;
import com.nimius.smartbus.views.service.repository.CarsIntentService;
import com.nimius.smartbus.views.service.repository.SmartToursIntentService;
import com.nimius.smartbus.views.service.repository.ToursIntentService;
import com.nimius.smartbus.views.utility.AppPreferences;
import com.nimius.smartbus.views.utility.Constants;
import com.nimius.smartbus.views.utility.Utils;

import java.util.Timer;
import java.util.TimerTask;


public class HomeChildFragment extends AuthenticateApiFragment implements OnMapReadyCallback {
    private static int UPDATE_INTERVAL = 30000; // 30 sec
    private static int TIME_INTERVAL = 30000; // 30 sec
    ReplaceFragmentCallback replaceFragmentCallback;
    LocationDeniedCallback locationDeniedCallback;
    ButtonClickCallback buttonClickCallback;
    FragmentHomeChildBinding homeChildBinding;
    AppPreferences appPreferences;
    private GoogleMap mMap;
    private ImageView[] dots;
    private Context mContext;

    BookingIdModel bookingIdModel;
    MarkerOptions currentOptionMarker, busOptionMarker, markerPickup;
    Marker markerCurrent;
    Marker markerBus;
    private int selectedPosition = -1;
    private int pageCount;
    CarToursDetailReceiver carDetailReceiver;
    SmartToursDetailReceiver smartToursDetailReceiver;
    BookingDetailReceiver bookingDetailReceiver;
    TourDetailReceiver tourDetailReceiver;
    Timer bookingTimer, tourTimer;
    int counter;
    private int passengers;
//    private boolean tourTimerActivated;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        replaceFragmentCallback = (ReplaceFragmentCallback) context;
        buttonClickCallback = (ButtonClickCallback) context;
        locationDeniedCallback = (LocationDeniedCallback) context;
        mContext = context;
    }


    public static HomeChildFragment newInstance(boolean isNewBooking, BookingIdModel bookingList, int pageCount, int position) {
        HomeChildFragment homeChildFragment = new HomeChildFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_BOOKING_DETAIL, bookingList);
        bundle.putBoolean(Constants.EXTRA_NEW_BOOKING, isNewBooking);
        bundle.putInt(Constants.EXTRA_POSITION, position);
        bundle.putInt("PageCount", pageCount);
        homeChildFragment.setArguments(bundle);

        return homeChildFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setView(R.layout.fragment_home_child, true);

        /****
         * INITIALIZATION OF MAP
         */
        MapsInitializer.initialize(mContext);


    }


    /**
     * INNIT VARIABLES
     */
    @Override
    public void initVariable() {
        homeChildBinding = getBinding();
        homeChildBinding.setHomeChildFragment(this);
        appPreferences = new AppPreferences(mContext);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void loadData() {

        //ASK LOCATION PERMISSION
        locationPermission();

        Bundle bundle = getArguments();
        if (bundle != null) {
            pageCount = bundle.getInt("PageCount");
            bookingIdModel = bundle.getParcelable(Constants.EXTRA_BOOKING_DETAIL);
            selectedPosition = bundle.getInt(Constants.EXTRA_POSITION);
            Log.e(TAG, "loadData: selectedPosition" + selectedPosition);
        }

        /**UNREGISTER RECEIVERS AND CANCEL THE TIMERS**/
        unregisterReceiver_cancelTimer();

        /**REGISTER RECEIVERS**/
        registerBookingReceiver();
        registerTourReceiver();
        registerSmartToursReceiver();
        registerCarsReceiver();


        if (pageCount > 0) {
            showWaitProgressbar(true);
            authenticateRequest(ApiConstant.query_booking + bookingIdModel.getBookingId());
            setUiPageViewController(selectedPosition);
        }


        Log.e(TAG, "loadData: database" + mContext.getDatabasePath("smaretbus").getAbsolutePath());


    }


    @Override
    public void permissionDenied(String message) {
        showSnackBar(mContext, message);
        locationDeniedCallback.permissionDenied(true);
    }

    @Override
    public void permissionsPermanentlyDeclined(String message) {
        showPermissionDialog(message, mContext);
        locationDeniedCallback.permissionDenied(true);
    }


    /***
     * UN-REGISTER BROADCAST AND CANCEL TIMER
     */
    private void unregisterReceiver_cancelTimer() {
        if (carDetailReceiver != null) {
            mContext.unregisterReceiver(carDetailReceiver);
        }
        if (smartToursDetailReceiver != null) {
            mContext.unregisterReceiver(smartToursDetailReceiver);
        }
        if (bookingDetailReceiver != null) {
            mContext.unregisterReceiver(bookingDetailReceiver);
        }

        if (tourDetailReceiver != null) {
            mContext.unregisterReceiver(tourDetailReceiver);
        }

        cancelSmartTourTimer();
        cancelBookingTimer();

    }

    /**
     * CANCEL SMART TOUR TIMER
     */
    private void cancelSmartTourTimer() {
        if (tourTimer != null) {
            Log.e(TAG, "loadData: tourTimer cancel");
            tourTimer.cancel();
            tourTimer = null;
        }

    }

    /***
     * CANCEL BOOKING TIMER
     */
    private void cancelBookingTimer() {
        if (bookingTimer != null) {
            Log.e(TAG, "loadData: bookingTimer cancel");
            bookingTimer.cancel();
            bookingTimer = null;
        }
    }


    /**
     * CLICK EVENT LISTENER
     *
     * @param view
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_another_pick:
                AuthenticateFragment authenticateFragment = new AuthenticateFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.EXTRA_NEW_BOOKING, true);
                authenticateFragment.setArguments(bundle);
                replaceFragmentCallback.changeFragment(authenticateFragment, "");
                appPreferences.setCurrentPagePosition(selectedPosition);
                break;

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /***
     * UPDATE USER CURRENT LOCATION
     * @param location
     */
    @Override
    public void onHandleNewLocation(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentOptionMarker = new MarkerOptions();
        currentOptionMarker.position(latLng);
        currentOptionMarker.title("Current location");
        currentOptionMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));

        if (markerCurrent != null) {
            markerCurrent.remove();
        }
        markerCurrent = mMap.addMarker(currentOptionMarker);
        markerCurrent.setPosition(latLng);

//        CameraPosition newCamPos = new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()),
//                18.5f,
//                mMap.getCameraPosition().tilt, //use old tilt
//                mMap.getCameraPosition().bearing); //use old bearing
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
    }


    /***
     * UPDATE BUS LOCATION
     * @param latitude
     * @param longitude
     */
    private void updateBusLocation(double latitude, double longitude) {

        Log.e(TAG, "car location: latitude" + latitude);
        Log.e(TAG, "car location: longitude" + longitude);
        LatLng latLngBus = new LatLng(latitude, longitude);
        busOptionMarker = new MarkerOptions();
        busOptionMarker.position(latLngBus);


//        busOptionMarker.title("Bus location");
        busOptionMarker.zIndex(1.0f);
        busOptionMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_location));
        if (markerBus != null) {
            markerBus.remove();
        }
        markerBus = mMap.addMarker(busOptionMarker);
        markerBus.setPosition(latLngBus);

//        CameraPosition newCamPos = new CameraPosition(new LatLng(latitude, longitude),
//                18.5f,
//                mMap.getCameraPosition().tilt, //use old tilt
//                mMap.getCameraPosition().bearing); //use old bearing
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
    }


    /**
     * UPDATE PICK UP LOCATION
     */
    private void updatePickUpLocation(double latitude, double longitude, String title) {
        LatLng latLngPickup = new LatLng(latitude, longitude);
        markerPickup = new MarkerOptions();
        markerPickup.position(latLngPickup);
        markerPickup.title(title);
        markerPickup.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup));
        mMap.addMarker(markerPickup);


        CameraPosition newCamPos = new CameraPosition(new LatLng(latitude, longitude),
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

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                info.addView(title);
                return info;
            }
        });
    }


    /****
     * UPDATE BOOKING INFORMATION DATE TIME FROM BOOKING API AND CALL TOUR AND SMART TOUR API
     * @param model
     */
    private void updatePickupDetail_BookingInfoDateTime(BookingModel model) {
        /**booking information date time**/
        homeChildBinding.tvBookingDatetime.setText(Utils.parseBookingDateTime(model.startDateTime, mContext));

        /**passenger counter**/
        if (model.pickupPlace != null) {
            passengers = model.ageGroups.adultModel.quantity;
        }

        /**generate the barcode from booking ID***/
        try {
            Bitmap bitmap = encodeAsBitmap(String.valueOf(model.id));
            homeChildBinding.ivPickupLogo.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


        /**call tour api to get the booking information detail**/
        if (!TextUtils.isEmpty(model.tour)) {
            cancelBookingTimer();
            showWaitProgressbar(true);
            toursApiRequest(model.tour);
        } else {
            setBookingTimer(model.url);
            /**set the timer if tour api is not exists**/
            homeChildBinding.tvBookingDetail.setText("");
        }


        /**call smart tour api***/
        if (!TextUtils.isEmpty(model.smartTour)) {
            showWaitProgressbar(true);
            smartToursApiRequest(model.smartTour);
//            setSmartToursTimer(model.smartTour, model.startDateTime);
        } else {
            hideWaitProgressbar();
            ///pending
        }

    }

    /***
     * UPDATE TOUR INFORMATION FROM BOOKING TIMER
     * @param model
     */
    private void updateTourFromBookingTimer(BookingModel model) {

        /**call tour api to get the booking information detail**/
        if (!TextUtils.isEmpty(model.tour)) {
            cancelBookingTimer();
            callToursIntent(model.tour);
        } else {
            /**set the timer if tour api is not exists**/
            homeChildBinding.tvBookingDetail.setText("");
        }
    }

    /***
     * UPDATE BOOKING INFORMATION TITLE FROM TOUR API
     * @param model
     */
    private void updateBookingInfoTitle(BookingTourModel model) {
        homeChildBinding.tvBookingDetail.setText(model.activity.name);
    }


    /***
     * UPDATE PICKUP DETAIL FROM SMART TOUR API
     * @param model
     */
    private void updatePickupDetails(BookingTourModel model) {
        if (model != null)
            setTourStatus(model.status, homeChildBinding.tvBusStatus);

        if (model != null && model.place != null) {
            String label = model.place.title + " " + getString(R.string.str_reykjavik_terminal) + "\n" +
                    getString(R.string.str_pick_up_start) + " " + Utils.parsePickUpDateTime(model.datetime, mContext) + " \n" +
                    getString(R.string.str_for) + " " + passengers + " " +
                    getString(R.string.str_passengers);
            homeChildBinding.tvPickupDetail.setText(label);

            updatePickUpLocation(model.place.location.latitude, model.place.location.longitude, model.place.title);

        } else {
            homeChildBinding.tvPickupDetail.setText("");
        }


        /***
         * SET THE SMART TOUR TIMER FOR BUST STATUS
         */
        if (model != null)
            setSmartToursTimer(model.url, model.datetime);


        if (model != null && model.cars.size() > 0) {
//                if (!tourTimerActivated) {
//                    showWaitProgressbar(true);
//                }
            callCarIntent(model.cars.get(0).url);

            /***set the timer once bus has been started**/
            if (model.status.equalsIgnoreCase(Constants.FINISHED)) {
                cancelSmartTourTimer();
            }

        } else {
            hideWaitProgressbar();
            homeChildBinding.tvBusNumber.setText("");
            /**remove the bus marker also**/
            if (markerBus != null) {
                markerBus.remove();
            }
        }

    }


    /**
     * UPDATE PICKUP DETAIL
     * Here we have to less 30 minutes from pickup start.
     * all the detail is to be fetch from booking api
     * <p>
     * ON 1st attempt it will call tour api and car api as well as we'll set the timer for tour api
     * inside timer we'll check the condition if departure time and current time diff is <=30 then
     * then we'll call tour api every 30 second to get update status along with tour api we'll
     * also make an call for car api which is based on tour api.
     * Once we receive status as FINISH then we'll stop the timer and it will consider that tour is
     * departed.
     *
     * @param model
     */


    /**
     * UPDATE BOOKING DETAIL
     * all the detail is to be fetch from tour api
     */

    private void updateBusStatusFromSmartTourTimer(BookingTourModel tourModel) {
        if (tourModel != null) {
            setTourStatus(tourModel.status, homeChildBinding.tvBusStatus);

            if (tourModel.cars.size() > 0) {
                callCarIntent(tourModel.cars.get(0).url);
                /***set the timer once bus has been started**/
                if (tourModel.status.equalsIgnoreCase(Constants.FINISHED)) {
                    cancelSmartTourTimer();
                }

            } else {
                hideWaitProgressbar();
                homeChildBinding.tvBusNumber.setText("");
                /**remove the bus marker also**/
                if (markerBus != null) {
                    markerBus.remove();
                }
            }
        }
    }


    /**
     * UPDATE CARD DETAIL , LIKE NUMBER PLATE CAR TYPE
     * all the detail is to be fetch from car api
     */
    private void updateCarsLocationFromCarApi(CarDetailModel model) {
        if (model != null) {
            homeChildBinding.tvBusNumber.setText(getString(R.string.str_bus_number) + "   " + model.plateNumber);

            if (model.currentLocation != null) {
                updateBusLocation(model.currentLocation.latitude, model.currentLocation.longitude);
            }
            hideWaitProgressbar();
        }

    }

    /***
     * SET THE TOUR STATUS
     * @param status
     * @param tvStatus
     */
    private void setTourStatus(String status, TextView tvStatus) {
        if (mContext == null) {
            return;
        }
        if (status.equalsIgnoreCase(Constants.NOT_ASSIGNED)) {
            tvStatus.setText(getString(R.string.msg_bus_not_assigned));
            homeChildBinding.tvBusStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_button_text));
        } else if (status.equalsIgnoreCase(Constants.ASSIGNED)) {
            tvStatus.setText(getString(R.string.msg_bus_assigned));
            homeChildBinding.tvBusStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_button_text));
        } else if (status.equalsIgnoreCase(Constants.STARTED)) {
            tvStatus.setText(getString(R.string.msg_bus_started));
            homeChildBinding.tvBusStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_button_text));
        } else if (status.equalsIgnoreCase(Constants.BOARDING)) {
            tvStatus.setText(getString(R.string.msg_bus_boarding));
            homeChildBinding.tvBusStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_button_text));
        } else if (status.equalsIgnoreCase(Constants.EN_ROUTE)) {
            tvStatus.setText(getString(R.string.msg_bus_en_route));
            homeChildBinding.tvBusStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_button_text));
        } else if (status.equalsIgnoreCase(Constants.FINISHED)) {
            tvStatus.setText(getString(R.string.msg_bus_finished));
            homeChildBinding.tvBusStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_grey_button_text));
        }
    }


    @Override
    public void onTourSuccess(BookingTourModel model) {
        if (!isAdded()) {
            return;
        }
        if (model != null)
            updateBookingInfoTitle(model);
    }


    @Override
    public void onAuthenticateSuccess(BookingModel model) {
        if (!isAdded()) {
            return;
        }
        if (model != null)
            updatePickupDetail_BookingInfoDateTime(model);
    }

    @Override
    public void onFailed(String message) {
        if (!isAdded()) {
            return;
        }
        hideWaitProgressbar();
    }

    @Override
    public void onSmartTourSuccess(BookingTourModel model) {
        if (!isAdded()) {
            return;
        }
        updatePickupDetails(model);
    }

    /***
     * REGISTER BOOKING  BROADCAST
     */
    private void registerBookingReceiver() {
        Log.e(TAG, "interval registerBookingReceiver: ");
        bookingDetailReceiver = new BookingDetailReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BOOKING_DETAILS_BROADCAST_FILTER);
        mContext.registerReceiver(bookingDetailReceiver, intentFilter);
    }


    /***
     * REGISTER TOUR  BROADCAST
     */
    private void registerTourReceiver() {
        Log.e(TAG, "interval registerTourReceiver: ");
        tourDetailReceiver = new TourDetailReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.TOURS_DETAILS_BROADCAST_FILTER);
        mContext.registerReceiver(tourDetailReceiver, intentFilter);
    }

    /***
     * REGISTER TOURS  BROADCAST
     */

    private void registerSmartToursReceiver() {
        Log.e(TAG, "interval registerSmartToursReceiver: ");
        smartToursDetailReceiver = new SmartToursDetailReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SMART_TOURS_DETAILS_BROADCAST_FILTER);
        mContext.registerReceiver(smartToursDetailReceiver, intentFilter);
    }


    /***
     * REGISTER CAR  BROADCAST
     */
    private void registerCarsReceiver() {
        Log.e(TAG, "interval registerCarsReceiver: ");
        carDetailReceiver = new CarToursDetailReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CAR_DETAILS_BROADCAST_FILTER);
        mContext.registerReceiver(carDetailReceiver, intentFilter);
    }


    /***
     * BOOKING BROADCAST RECEIVER
     */
    private class BookingDetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "interval SmartToursDetailReceiver: ");
            if (!isAdded()) {
                return;
            }
            BookingModel model = intent.getParcelableExtra(BookingIntentService.EXTRA_BOOKING_API);
            int code = intent.getIntExtra(SmartToursIntentService.EXTRA_CODE, 0);
            if (code == CarsIntentService.CODE_SUCCESS) {
                if (model != null) {
                    updateTourFromBookingTimer(model);
                }
            } else if (code == SmartToursIntentService.CODE_ERROR) {
                if (model != null)
                    showSnackBar(mContext, model.errorMessage);

            } else if (code == SmartToursIntentService.CODE_FAILURE) {
                showErrorMessage(getString(R.string.msg_server_error_try_again));

            } else {
                if (code == SmartToursIntentService.CODE_INTERNET) {
                    showSnackBar(mContext, getString(R.string.msg_no_internet));
                }
            }
        }
    }


    /***
     * TOUR BROADCAST RECEIVER
     */
    private class TourDetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "interval SmartToursDetailReceiver: ");
            if (!isAdded()) {
                return;
            }
            BookingTourModel model = intent.getParcelableExtra(ToursIntentService.EXTRA_TOURS_API);
            int code = intent.getIntExtra(SmartToursIntentService.EXTRA_CODE, 0);
            if (code == CarsIntentService.CODE_SUCCESS) {
                if (model != null) {
                    updateBookingInfoTitle(model);
                }
            } else if (code == SmartToursIntentService.CODE_ERROR) {
                if (model != null)
                    showSnackBar(mContext, model.errorMessage);

            } else if (code == SmartToursIntentService.CODE_FAILURE) {
                showErrorMessage(getString(R.string.msg_server_error_try_again));

            } else {
                if (code == SmartToursIntentService.CODE_INTERNET) {
                    showSnackBar(mContext, getString(R.string.msg_no_internet));
                }
            }
        }
    }


    /***
     * SMART TOUR BROADCAST ON RECEIVER
     */
    private class SmartToursDetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "interval SmartToursDetailReceiver: ");
            if (!isAdded()) {
                return;
            }
            BookingTourModel model = intent.getParcelableExtra(SmartToursIntentService.EXTRA_SMART_TOURS_API);

            int code = intent.getIntExtra(SmartToursIntentService.EXTRA_CODE, 0);
            if (code == CarsIntentService.CODE_SUCCESS) {
                if (model != null)
                    updateBusStatusFromSmartTourTimer(model);
            } else if (code == SmartToursIntentService.CODE_ERROR) {
                if (model != null)
                    showSnackBar(mContext, model.errorMessage);

            } else if (code == SmartToursIntentService.CODE_FAILURE) {
                showErrorMessage(getString(R.string.msg_server_error_try_again));

            } else {
                if (code == SmartToursIntentService.CODE_INTERNET) {
                    showSnackBar(mContext, getString(R.string.msg_no_internet));
                }
            }
        }
    }

    /***
     * CAR BROADCAST ON RECEIVER
     */
    private class CarToursDetailReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "interval CarToursDetailReceiver: ");
            if (!isAdded()) {
                return;
            }
            int code = intent.getIntExtra(CarsIntentService.EXTRA_CODE, 0);
            CarDetailModel model = intent.getParcelableExtra(CarsIntentService.EXTRA_CARS_API);
            if (code == CarsIntentService.CODE_SUCCESS) {
                if (model != null)
                    updateCarsLocationFromCarApi(model);

            } else if (code == CarsIntentService.CODE_ERROR) {
                if (model != null)
                    showSnackBar(mContext, model.errorMessage);

            } else if (code == CarsIntentService.CODE_FAILURE) {
                showErrorMessage(getString(R.string.msg_server_error_try_again));

            } else {
                if (code == CarsIntentService.CODE_INTERNET) {
                    showSnackBar(mContext, getString(R.string.msg_no_internet));
                }
            }
        }
    }


    /***
     * SET BOOKING TIMER IN TOUR IS NOT AVAIL
     * @param bookingUrl
     */
    public void setBookingTimer(String bookingUrl) {
        Log.e(TAG, "interval setBookingTimer: started");
        //Declare the timer
        tourTimer = new Timer();
//Set the schedule function and rate
        tourTimer.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
                                              //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                              Log.e(TAG, " interval tour run: Timer set" + counter);
                                              Log.e(TAG, " interval tour selectedPosition: " + selectedPosition);
                                              callBookingIntent(bookingUrl);
                                          }

                                      },
//Set how long before to start calling the TimerTask (in milliseconds)
                UPDATE_INTERVAL,
//Set the amount of time between each execution (in milliseconds)
                TIME_INTERVAL);
    }


    /***
     * SET TIMER FOR TOUR API
     * @param carUrl
     */
    public void setSmartToursTimer(String carUrl, String dateTime) {
        Log.e(TAG, "interval setSmartToursTimer: started");
        //Declare the timer
        tourTimer = new Timer();
//Set the schedule function and rate
        tourTimer.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
                                              //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                              Log.e(TAG, " interval tour run: Timer set" + counter);
                                              Log.e(TAG, " interval tour selectedPosition: " + selectedPosition);

                                              /***if difference is <=30 minutes then it will make an call for smart tour api***/
                                              if (Utils.startTourTimerDateTime(dateTime)) {
//                                                  tourTimerActivated = true;
                                                  Log.e(TAG, "interval tour timer set: ");
                                                  callSmartToursIntent(carUrl);
                                              }
                                              counter++;
                                          }

                                      },
//Set how long before to start calling the TimerTask (in milliseconds)
                UPDATE_INTERVAL,
//Set the amount of time between each execution (in milliseconds)
                TIME_INTERVAL);
    }


    /***
     * SET PAGER INDICATOR BASE ON PAGE COUNT
     */
    private void setUiPageViewController(int position) {
        if (pageCount == 1) {
            return;
        }
        dots = new ImageView[pageCount];
        homeChildBinding.viewPagerCountDots.removeAllViews();
        for (int i = 0; i < pageCount; i++) {
            dots[i] = new ImageView(mContext);
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pager_selected_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.pager_unselected_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);


            homeChildBinding.viewPagerCountDots.addView(dots[i], params);
        }
        homeChildBinding.viewPagerCountDots.invalidate();

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: HomeScreen");
//        unregisterReceiver_cancelTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: HomeScreen");
        unregisterReceiver_cancelTimer();

    }

}
