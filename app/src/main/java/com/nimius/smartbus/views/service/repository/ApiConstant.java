package com.nimius.smartbus.views.service.repository;

public class ApiConstant {


    public static final String BASE_URL_PLACE_API = "https://kerfi.airportdirect.is/places/?format=json";

    /**
     * PRISMIC PARAMS
     */
    public static final String ref = "ref";
    public static final String access_token = "access_token";
    public static final String query = "q";


    public static final String prismic_ref_key="https://smartbus.cdn.prismic.io/api/v2";

    public static final String documentId_pickup = "[[at(document.id,\"W1BmYyUAACgAFMvz\")]]";
    public static final String documentId_terminal = "[[at(document.id,\"W1BmFiUAACYAFMtM\")]]";
    public static final String documentId_contact_us = "[[at(document.id,\"W1BmqSUAACgAFM0s\")]]";
    public static final String documentId_missed_pickup = "[[at(document.id,\"W1BmTyUAACYAFMvH\")]]";

    public static final String ref_value = "W0YE-SMAAKkA4SNk";
    public static final String access_token_value = "MC5XMUJtMHlVQUFDUUFGTTNw.Z--_vTBpfe-_ve-_ve-_vTB377-9eu-_ve-_vQgpQW0uSCbvv73vv71Q77-977-9ZHF077-977-977-9";


    /***
     * BOOKING API PARAMS
     */
    public static final String date_from = "date_from";
    public static final String date_to = "date_to";
    public static final String status = "status";
    public static final String payment_status = "payment_status";
    public static final String customer_id = "customer_id";
    public static final String customer_name = "customer_name";
    public static final String customer_email = "customer_email";

    public static final String query_booking = "bookings/";


    public static final String param_places = "places/";
    public static final String param_format = "?format=json";
}
