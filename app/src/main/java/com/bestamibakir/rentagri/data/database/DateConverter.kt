package com.bestamibakir.rentagri.data.database

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.Date

class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromFirebaseTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it / 1000, ((it % 1000) * 1000000).toInt()) }
    }

    @TypeConverter
    fun firebaseTimestampToLong(timestamp: Timestamp?): Long? {
        return timestamp?.let { it.seconds * 1000 + it.nanoseconds / 1000000 }
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return if (value.isNullOrEmpty()) {
            emptyList()
        } else {
            value.split(",").filter { it.isNotEmpty() }
        }
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }
} 