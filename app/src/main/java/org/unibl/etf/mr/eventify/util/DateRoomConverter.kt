package org.unibl.etf.mr.eventify.util

import androidx.room.TypeConverter
import java.util.Date

class DateRoomConverter {

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return value?.time
    }
}
