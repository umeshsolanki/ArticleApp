package com.umesh.discoverindia.db;

import androidx.room.TypeConverter;

import java.util.Date;

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value:Long?):Date? {
            return value?.let {
                Date(it)
            }
        }

        @TypeConverter
        @JvmStatic
        fun dateToTimestamp(date:Date?):Long? {
            return date?.getTime();
        }
    }
}