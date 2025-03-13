package com.jk24.decimalclock.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Represents date values in various formats.
 * Provides a consistent interface for working with dates across the app.
 */
data class DateModel(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val dayOfYear: Int
) {
    companion object {
        /**
         * Creates a DateModel instance from the current date.
         */
        fun createFromCurrentDate(): DateModel {
            val calendar = Calendar.getInstance()
            return createFromCalendar(calendar)
        }
        
        /**
         * Creates a DateModel instance from a Calendar object.
         */
        fun createFromCalendar(calendar: Calendar): DateModel {
            return DateModel(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.DAY_OF_YEAR)
            )
        }
    }
    
    /**
     * Formats standard date as YYYY-MM-DD.
     */
    fun formatStandardDate(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    
    /**
     * Formats decimal date as "YYYY DDD days"
     */
    fun formatDecimalDate(): String {
        return "$year $dayOfYear days"
    }
    
    /**
     * Creates a calendar instance from this model.
     */
    fun toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return calendar
    }
}