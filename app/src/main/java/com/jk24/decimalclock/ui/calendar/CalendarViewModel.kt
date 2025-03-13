package com.jk24.decimalclock.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.repository.TimeConversionRepository
import com.jk24.decimalclock.domain.usecase.GetSelectedDateTimeUseCase
import com.jk24.decimalclock.domain.usecase.SaveSelectedDateTimeUseCase
import java.util.Calendar
import java.util.Locale

/**
 * Handles date and time data for the Calendar screen.
 * Manages calculations and formatting for standard and decimal time representations.
 */
class CalendarViewModel(
    private val timeConversionRepository: TimeConversionRepository,
    private val getSelectedDateTimeUseCase: GetSelectedDateTimeUseCase,
    private val saveSelectedDateTimeUseCase: SaveSelectedDateTimeUseCase
) : ViewModel() {
    /**
     * Stores and manipulates the selected date and time.
     */
    private var currentDateTime: DateTimeModel = DateTimeModel.createFromCurrentDateTime()
    
    /**
     * LiveData for the selected standard date/time
     */
    private val _standardDateTime = MutableLiveData<String>()
    val standardDateTime: LiveData<String> = _standardDateTime
    
    /**
     * LiveData for the selected decimal date/time
     */
    private val _decimalDateTime = MutableLiveData<String>()
    val decimalDateTime: LiveData<String> = _decimalDateTime
    
    /**
     * Indicate if a saved selection exists
     */
    private val _hasSelection = MutableLiveData<Boolean>()
    val hasSelection: LiveData<Boolean> = _hasSelection
    
    /**
     * Initialize with current date and time.
     */
    init {
        loadSavedDateTime()
    }
    
    /**
     * Loads any previously saved date/time
     */
    private fun loadSavedDateTime() {
        _hasSelection.value = getSelectedDateTimeUseCase.hasSelection()
        
        if (_hasSelection.value == true) {
            getSelectedDateTimeUseCase.execute()?.let { dateTime ->
                currentDateTime = dateTime
                updateDisplays()
            }
        }
    }
    
    /**
     * Update the year, month, and day
     */
    fun setSelectedDate(year: Int, month: Int, day: Int) {
        val calendar = currentDateTime.toCalendar()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        
        currentDateTime = DateTimeModel.createFromCalendar(calendar)
        updateDisplays()
        saveCurrentDateTime()
    }
    
    /**
     * Update the hour and minute
     */
    fun setSelectedTime(hourOfDay: Int, minute: Int) {
        val calendar = currentDateTime.toCalendar()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        
        currentDateTime = DateTimeModel.createFromCalendar(calendar)
        updateDisplays()
        saveCurrentDateTime()
    }
    
    /**
     * Save the current date time selection
     */
    private fun saveCurrentDateTime() {
        saveSelectedDateTimeUseCase.execute(currentDateTime)
        _hasSelection.value = true
    }
    
    /**
     * Update UI displays with current values
     */
    private fun updateDisplays() {
        val calendar = currentDateTime.toCalendar()
        
        // Update standard format - date and time on separate lines
        // Year, month, day on first line, hour and minute on second line (no seconds)
        val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val timeFormatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()) 
        
        _standardDateTime.value = "${dateFormatter.format(calendar.time)}\n${timeFormatter.format(calendar.time)}"
        
        // Update decimal format with 3 decimal places
        _decimalDateTime.value = currentDateTime.formatCombinedDecimal(3)
    }
    
    /**
     * Gets the current calendar
     */
    fun getCurrentCalendar(): Calendar {
        return currentDateTime.toCalendar()
    }
    
    /**
     * Factory for creating instances of this ViewModel.
     */
    class Factory(
        private val timeConversionRepository: TimeConversionRepository,
        private val getSelectedDateTimeUseCase: GetSelectedDateTimeUseCase,
        private val saveSelectedDateTimeUseCase: SaveSelectedDateTimeUseCase
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(
                    timeConversionRepository,
                    getSelectedDateTimeUseCase,
                    saveSelectedDateTimeUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}