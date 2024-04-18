package com.example.weatherapp.data.repository

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

object Converters {
    @TypeConverter
    @JvmStatic
    fun dateTimeToString(dateTime: LocalDateTime?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @TypeConverter
    @JvmStatic
    fun stringToDateTime(str: String?) = str?.let {
        LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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