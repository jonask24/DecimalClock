package com.jk24.decimalclock.domain.model

/**
 * Represents time values in various formats.
 * Provides a consistent interface for working with time across the app.
 */
data class TimeModel(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val decimalValue: Double
) {
    companion object {
        /**
         * Creates a TimeModel instance from current system time.
         */
        fun createFromCurrentTime(): TimeModel {
            val calendar = java.util.Calendar.getInstance()
            val hours = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(java.util.Calendar.MINUTE)
            val seconds = calendar.get(java.util.Calendar.SECOND)
            
            // Calculate decimal value
            val secondsInDay = 24 * 60 * 60
            val currentSeconds = hours * 3600 + minutes * 60 + seconds
            val decimalValue = currentSeconds.toDouble() / secondsInDay.toDouble()
            
            return TimeModel(hours, minutes, seconds, decimalValue)
        }
        
        /**
         * Creates a TimeModel instance from a decimal time value.
         * 
         * @param decimalTime Value between 0.0 and 1.0 representing fraction of day
         */
        fun createFromDecimal(decimalTime: Double): TimeModel {
            val totalSeconds = (decimalTime * 24 * 60 * 60).toInt()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            
            return TimeModel(hours, minutes, seconds, decimalTime)
        }
        
        /**
         * Creates a TimeModel from elapsed milliseconds.
         */
        fun createFromMillis(milliseconds: Long): TimeModel {
            val seconds = milliseconds / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            
            val secondsInDay = 24 * 60 * 60
            val totalSeconds = seconds % secondsInDay
            val decimalValue = totalSeconds.toDouble() / secondsInDay.toDouble()
            
            return TimeModel(hours.toInt() % 24, (minutes % 60).toInt(), (seconds % 60).toInt(), decimalValue)
        }
    }
    
    /**
     * Formats standard time as HH:MM:SS.
     */
    fun formatStandardTime(): String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    /**
     * Formats decimal time with specified precision.
     */
    fun formatDecimalTime(precision: Int = 5): String {
        return String.format("%.${precision}f days", decimalValue)
    }
}