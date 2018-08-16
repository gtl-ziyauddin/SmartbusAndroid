package com.nimius.smartbus.views.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "booking_detail")
public class BookingIdModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int uid;


    @ColumnInfo(name = "booking_id")
    private String bookingId;


    @ColumnInfo(name = "customer_lastname")
    private String customerLastName;

    @ColumnInfo(name = "customer_name")
    private String customerName;

    @ColumnInfo(name = "customer_email")
    private String customerEmail;

    @ColumnInfo(name = "start_datetime")
    private String startDateTime;



    public BookingIdModel(String bookingId, String customerLastName,
                          String customerName, String customerEmail,String startDateTime) {
        this.bookingId = bookingId;
        this.customerLastName = customerLastName;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDateTime=startDateTime;

    }


    protected BookingIdModel(Parcel in) {
        uid = in.readInt();
        bookingId = in.readString();
        customerLastName = in.readString();
        startDateTime = in.readString();
        customerName = in.readString();
        customerEmail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(bookingId);
        dest.writeString(customerLastName);
        dest.writeString(startDateTime);
        dest.writeString(customerName);
        dest.writeString(customerEmail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingIdModel> CREATOR = new Creator<BookingIdModel>() {
        @Override
        public BookingIdModel createFromParcel(Parcel in) {
            return new BookingIdModel(in);
        }

        @Override
        public BookingIdModel[] newArray(int size) {
            return new BookingIdModel[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
