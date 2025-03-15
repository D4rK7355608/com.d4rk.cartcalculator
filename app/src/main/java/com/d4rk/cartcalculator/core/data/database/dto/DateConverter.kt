package com.d4rk.cartcalculator.core.data.database.dto

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun toDate(dateLong : Long?) : Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date : Date?) : Long? {
        return date?.time
    }
}