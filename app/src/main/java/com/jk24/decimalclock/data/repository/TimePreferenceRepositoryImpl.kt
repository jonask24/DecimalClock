package com.jk24.decimalclock.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.repository.TimePreferenceRepository
import java.util.Calendar

/**
 * Implementation of TimePreferenceRepository using SharedPreferences.
 * Handles persistent storage of time-related user preferences.
 */
class TimePreferenceRepositoryImpl(context: Context) : TimePreferenceRepository {
    /**
     * SharedPreferences instance for data storage.
     */
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "DecimalClockPrefs"
        private const val KEY_SELECTED_DATE_TIME = "selected_date_time"
    }
    
    /**
     * Saves selected date and time to persistent storage.
     */
    override fun saveSelectedDateTime(dateTime: DateTimeModel) {
        val calendar = dateTime.toCalendar()
        preferences.edit().putLong(KEY_SELECTED_DATE_TIME, calendar.timeInMillis).apply()
    }
    
    /**
     * Retrieves previously selected date and time.
     * @return DateTimeModel or null if none saved
     */
    override fun getSelectedDateTime(): DateTimeModel? {
        val timeMillis = preferences.getLong(KEY_SELECTED_DATE_TIME, -1)
        if (timeMillis == -1L) {
            return null
        }
        
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        return DateTimeModel.createFromCalendar(calendar)
    }
    
    /**
     * Checks if a date/time preference exists.
     * @return True if a date/time has been saved, false otherwise
     */
    override fun hasSelectedDateTime(): Boolean {
        return preferences.contains(KEY_SELECTED_DATE_TIME)
    }
    
    /**
     * Clears saved date/time preference.
     */
    override fun clearSelectedDateTime() {
        preferences.edit().remove(KEY_SELECTED_DATE_TIME).apply()
    }
}