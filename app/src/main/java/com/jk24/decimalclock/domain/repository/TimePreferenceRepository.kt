package com.jk24.decimalclock.domain.repository

import com.jk24.decimalclock.domain.model.DateTimeModel
import java.util.Calendar

/**
 * Repository interface for time preferences.
 * Defines operations for saving and retrieving time-related user preferences.
 */
interface TimePreferenceRepository {
    /**
     * Saves selected date and time to persistent storage.
     */
    fun saveSelectedDateTime(dateTime: DateTimeModel)
    
    /**
     * Retrieves previously selected date and time.
     * @return DateTimeModel or null if none saved
     */
    fun getSelectedDateTime(): DateTimeModel?
    
    /**
     * Checks if a date/time preference exists.
     * @return True if a date/time has been saved, false otherwise
     */
    fun hasSelectedDateTime(): Boolean
    
    /**
     * Clears saved date/time preference.
     */
    fun clearSelectedDateTime()
}