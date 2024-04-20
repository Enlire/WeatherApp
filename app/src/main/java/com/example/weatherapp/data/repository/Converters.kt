package com.example.weatherapp.data.repository

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object Converters {
    @TypeConverter
    @JvmStatic
    fun zonedDateTimeToString(dateTime: ZonedDateTime?) =
        dateTime?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)?.substring(0, 19)

    @TypeConverter
    @JvmStatic
    fun stringToZonedDateTime(str: String?) = str?.let {
        ZonedDateTime.parse(it, DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }

    @TypeConverter
    @JvmStatic
    fun stringToDate(str: String?) = str?.let {
        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    @JvmStatic
    fun dateToString(dateTime: LocalDate?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)
}