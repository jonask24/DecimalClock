package com.jk24.decimalclock.domain.repository

import com.jk24.decimalclock.domain.model.DateModel
import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.model.TimeModel
import java.util.Calendar

/**
 * Repository interface for time conversion operations.
 * Provides methods to convert between different time and date representations.
 */
interface TimeConversionRepository {
    /**
     * Gets the current time in various formats.
     * @return TimeModel with the current time.
     */
    fun getCurrentTime(): TimeModel
    
    /**
     * Gets the current date in various formats.
     * @return DateModel with the current date.
     */
    fun getCurrentDate(): DateModel
    
    /**
     * Gets combined current date and time.
     * @return DateTimeModel with current date and time.
     */
    fun getCurrentDateTime(): DateTimeModel
    
    /**
     * Converts standard time to decimal time.
     * @param hours Hours in 24-hour format
     * @param minutes Minutes
     * @param seconds Seconds
     * @return Value between 0.0 and 1.0 representing the fraction of the day
     */
    fun standardToDecimalTime(hours: Int, minutes: Int, seconds: Int): Double
    
    /**
     * Converts decimal time to standard time.
     * @param decimalTime Time as fraction of day (0.0-1.0)
     * @return TimeModel with standard and decimal time values
     */
    fun decimalToStandardTime(decimalTime: Double): TimeModel
    
    /**
     * Gets decimal date representation for a calendar date.
     * @param calendar Calendar instance to convert
     * @return DateModel with standard and decimal date values
     */
    fun getDecimalDate(calendar: Calendar): DateModel
}