package com.jk24.decimalclock.data.repository

import com.jk24.decimalclock.domain.model.DateModel
import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.model.TimeModel
import com.jk24.decimalclock.domain.repository.TimeConversionRepository
import java.util.Calendar

/**
 * Implementation of TimeConversionRepository.
 * Handles all time conversion operations.
 */
class TimeConversionRepositoryImpl : TimeConversionRepository {
    /**
     * Gets the current time in various formats.
     * @return TimeModel with the current time.
     */
    override fun getCurrentTime(): TimeModel {
        return TimeModel.createFromCurrentTime()
    }
    
    /**
     * Gets the current date in various formats.
     * @return DateModel with the current date.
     */
    override fun getCurrentDate(): DateModel {
        return DateModel.createFromCurrentDate()
    }
    
    /**
     * Gets combined current date and time.
     * @return DateTimeModel with current date and time.
     */
    override fun getCurrentDateTime(): DateTimeModel {
        return DateTimeModel.createFromCurrentDateTime()
    }
    
    /**
     * Converts standard time to decimal time.
     * @param hours Hours in 24-hour format
     * @param minutes Minutes
     * @param seconds Seconds
     * @return Value between 0.0 and 1.0 representing the fraction of the day
     */
    override fun standardToDecimalTime(hours: Int, minutes: Int, seconds: Int): Double {
        val secondsInDay = 24 * 60 * 60
        val currentSeconds = hours * 3600 + minutes * 60 + seconds
        return currentSeconds.toDouble() / secondsInDay.toDouble()
    }
    
    /**
     * Converts decimal time to standard time.
     * @param decimalTime Time as fraction of day (0.0-1.0)
     * @return TimeModel with standard and decimal time values
     */
    override fun decimalToStandardTime(decimalTime: Double): TimeModel {
        return TimeModel.createFromDecimal(decimalTime)
    }
    
    /**
     * Gets decimal date representation for a calendar date.
     * @param calendar Calendar instance to convert
     * @return DateModel with standard and decimal date values
     */
    override fun getDecimalDate(calendar: Calendar): DateModel {
        return DateModel.createFromCalendar(calendar)
    }
}