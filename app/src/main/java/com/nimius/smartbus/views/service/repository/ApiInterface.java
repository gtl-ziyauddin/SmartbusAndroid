package com.nimius.smartbus.views.service.repository;

import com.nimius.smartbus.views.service.model.PickupInfo.PickupInfoModel;
import com.nimius.smartbus.views.service.model.bookingModel.BookingModel;
import com.nimius.smartbus.views.service.model.bookingTourModel.BookingTourModel;
import com.nimius.smartbus.views.service.model.carDetailModel.CarDetailModel;
import com.nimius.smartbus.views.service.model.prismicModel.ProjectModel;
import com.nimius.smartbus.views.service.model.prismicModel.RefModel.RefKeyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {
    String BASE_URL_STATIC_CONTENT = "https://smartbus.cdn.prismic.io/api/v2/documents/";
    //    String BASE_URL_PLACE_API = "https://ops.airportdirect.reon.is/";

    /*https://kerfi.airportdirect.is/places/?format=json*/
    String BASE_URL_PLACE_API = "https://kerfi.airportdirect.is/";
    String BASE_URL_BOOKING_APP = "https://ops.airportdirect.reon.is/";


    String query_booking = "bookings";
    String query_search = "search";


    @GET(query_search)
    Call<ProjectModel> getContactUsPage(@Query(ApiConstant.ref) String ref,
                                        @Query(ApiConstant.access_token) String access_token,
                                        @Query(ApiConstant.query) String query);

//    @GET()
//    Call<List<BusPickupModel>> getPlacesApi(@Url String url);

    @GET()
    Call<List<PickupInfoModel>> getPlacesApi(@Url String url);



//    @GET(query_booking)
//    Call<BookingModel> authenticateApi(@Query(ApiConstant.date_from) String date_from, @Query(ApiConstant.date_to) String date_to,
//                                       @Query(ApiConstant.status) String status, @Query(ApiConstant.payment_status) String payment_status,
//                                       @Query(ApiConstant.customer_id) String customer_id, @Query(ApiConstant.customer_name) String customer_name,
//                                       @Query(ApiConstant.customer_email) String customer_email);

    @GET()
    Call<BookingModel> authenticateApi(@Url String url);


    @GET()
    Call<BookingTourModel> getTourApi(@Url String url);

    @GET()
    Call<BookingTourModel> getSmartTourApi(@Url String url);


    @GET()
    Call<CarDetailModel> getCarsDetailApi(@Url String url);


    @GET()
    Call<RefKeyModel> getPrismicRefApi(@Url String url);

}


