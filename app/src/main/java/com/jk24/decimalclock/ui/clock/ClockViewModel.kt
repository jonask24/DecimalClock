package com.jk24.decimalclock.ui.clock

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.usecase.GetCurrentDateTimeUseCase
import java.util.Date

/**
 * Manages data and calculations for clock displays.
 * Provides formatted time values in standard and decimal representations.
 */
class ClockViewModel(
   private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase
) : ViewModel() {
    /**
     * Standard time display data (HH:MM:SS format).
     */
    private val _standardTime = MutableLiveData<String>()
    val standardTime: LiveData<String> = _standardTime
    
    /**
     * Decimal time representation data.
     */
    private val _decimalTime = MutableLiveData<String>()
    val decimalTime: LiveData<String> = _decimalTime
    
    /**
     * Standard date display data (YYYY-MM-DD format).
     */
    private val _standardDate = MutableLiveData<String>()
    val standardDate: LiveData<String> = _standardDate
    
    /**
     * Decimal date representation data.
     */
    private val _decimalDate = MutableLiveData<String>()
    val decimalDate: LiveData<String> = _decimalDate
    
    /**
     * Combined decimal day+time representation.
     */
    private val _combinedDecimal = MutableLiveData<String>()
    val combinedDecimal: LiveData<String> = _combinedDecimal
    
    /**
     * Mixed representation of date and time.
     */
    private val _mixedDateTime = MutableLiveData<String>()
    val mixedDateTime: LiveData<String> = _mixedDateTime
    
    /**
     * Selected date/time data when applicable.
     */
    private val _selectedDateTime = MutableLiveData<Date?>()
    val selectedDateTime: LiveData<Date?> = _selectedDateTime
    
    /**
     * Handler for scheduling periodic updates.
     */
    private val handler = Handler(Looper.getMainLooper())
    
    /**
     * Runnable task that updates time displays.
     * Reschedules itself for periodic execution.
     */
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateTimeAndDate()
            handler.postDelayed(this, 1000) // Update every second
        }
    }
    
    /**
     * Starts periodic time updates.
     */
    fun startTimeUpdates() {
        updateTimeRunnable.run()
    }
    
    /**
     * Stops time updates to conserve resources.
     */
    fun stopTimeUpdates() {
        handler.removeCallbacks(updateTimeRunnable)
    }
    
    /**
     * Sets user-selected date time.
     * 
     * @param date Selected date, or null to clear
     */
    fun setSelectedDateTime(date: Date?) {
        _selectedDateTime.value = date
    }
    
    /**
     * Updates all time and date displays.
     */
    private fun updateTimeAndDate() {
        val dateTimeModel = getCurrentDateTimeUseCase.execute()
        
        // Update standard time
        _standardTime.value = dateTimeModel.time.formatStandardTime()
        
        // Update decimal time
        _decimalTime.value = dateTimeModel.time.formatDecimalTime()
        
        // Update standard date
        _standardDate.value = dateTimeModel.date.formatStandardDate()
        
        // Update decimal date
        _decimalDate.value = dateTimeModel.date.formatDecimalDate()
        
        // Update combined decimal
        _combinedDecimal.value = dateTimeModel.formatCombinedDecimal()
        
        // Update mixed view
        _mixedDateTime.value = dateTimeModel.formatMixedDateTime()
    }
    
    /**
     * Handles cleanup when ViewModel is being destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        stopTimeUpdates()
    }
    
    /**
     * Factory for creating instances of this ViewModel.
     */
    class Factory(private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClockViewModel::class.java)) {
                return ClockViewModel(getCurrentDateTimeUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}