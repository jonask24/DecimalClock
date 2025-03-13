package com.jk24.decimalclock.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Combines date and time representation.
 * Provides a unified interface for working with both date and time values.
 */
data class DateTimeModel(
    val date: DateModel,
    val time: TimeModel
) {
    companion object {
        /**
         * Creates a DateTimeModel instance from current system date and time.
         */
        fun createFromCurrentDateTime(): DateTimeModel {
            return DateTimeModel(
                DateModel.createFromCurrentDate(),
                TimeModel.createFromCurrentTime()
            )
        }
        
        /**
         * Creates a DateTimeModel instance from a Calendar object.
         */
        fun createFromCalendar(calendar: Calendar): DateTimeModel {
            val date = DateModel.createFromCalendar(calendar)
            
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            val seconds = calendar.get(Calendar.SECOND)
            
            // Calculate decimal value
            val secondsInDay = 24 * 60 * 60
            val currentSeconds = hours * 3600 + minutes * 60 + seconds
            val decimalValue = currentSeconds.toDouble() / secondsInDay.toDouble()
            
            val time = TimeModel(hours, minutes, seconds, decimalValue)
            
            return DateTimeModel(date, time)
        }
        
        /**
         * Creates a DateTimeModel instance from a Date object.
         */
        fun createFromDate(date: Date): DateTimeModel {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return createFromCalendar(calendar)
        }
    }
    
    /**
     * Formats date and time in a mixed format (MMM dd HH:mm:ss).
     */
    fun formatMixedDateTime(): String {
        val calendar = date.toCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, time.hours)
        calendar.set(Calendar.MINUTE, time.minutes)
        calendar.set(Calendar.SECOND, time.seconds)
        
        val format = SimpleDateFormat("MMM dd HH:mm:ss", Locale.getDefault())
        return format.format(calendar.time)
    }
    
    /**
     * Calculates and formats the combined decimal value (day of year + fraction of day).
     */
    fun formatCombinedDecimal(precision: Int = 5): String {
        val combinedValue = date.dayOfYear + time.decimalValue
        return String.format("%.${precision}f days", combinedValue)
    }
    
    /**
     * Converts to a Calendar instance.
     */
    fun toCalendar(): Calendar {
        val calendar = date.toCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, time.hours)
        calendar.set(Calendar.MINUTE, time.minutes)
        calendar.set(Calendar.SECOND, time.seconds)
        return calendar
    }
    
    /**
     * Converts to a Date instance.
     */
    fun toDate(): Date {
        return toCalendar().time
    }
}