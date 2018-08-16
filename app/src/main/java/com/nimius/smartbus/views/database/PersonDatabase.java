package com.nimius.smartbus.views.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {BookingIdModel.class}, version = 1)
public abstract class PersonDatabase extends RoomDatabase {
    public abstract BookingDao PersonDatabase();
}
