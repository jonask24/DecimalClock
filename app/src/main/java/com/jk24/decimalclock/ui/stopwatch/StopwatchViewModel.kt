package com.jk24.decimalclock.ui.stopwatch

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jk24.decimalclock.domain.model.TimeModel
import com.jk24.decimalclock.domain.repository.TimeConversionRepository
import java.util.Timer

/**
 * Manages stopwatch state and calculations.
 * Handles time tracking and formatting for stopwatch displays.
 */
class StopwatchViewModel(
    private val timeConversionRepository: TimeConversionRepository
) : ViewModel() {
    /**
     * Hours component for stopwatch display.
     */
    private val _hours = MutableLiveData("00")
    val hours: LiveData<String> = _hours
    
    /**
     * Minutes component for stopwatch display.
     */
    private val _minutes = MutableLiveData("00")
    val minutes: LiveData<String> = _minutes
    
    /**
     * Seconds component for stopwatch display.
     */
    private val _seconds = MutableLiveData("00")
    val seconds: LiveData<String> = _seconds
    
    /**
     * Decimal time representation.
     */
    private val _decimalTime = MutableLiveData("0.000")
    val decimalTime: LiveData<String> = _decimalTime
    
    /**
     * Running state of the stopwatch.
     */
    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning
    
    /**
     * System time when stopwatch was started.
     */
    private var startTime = 0L
    
    /**
     * Total time accumulated in milliseconds.
     */
    private var timeInMilliseconds = 0L
    
    /**
     * Current elapsed time in milliseconds.
     */
    private var elapsedTime = 0L
    
    /**
     * Time since last update in milliseconds.
     */
    private var updateTime = 0L
    
    /**
     * Handler for scheduling UI updates on main thread.
     */
    private val handler = Handler(Looper.getMainLooper())
    
    /**
     * Task that updates time values at regular intervals.
     */
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updateTime = SystemClock.uptimeMillis() - startTime
            elapsedTime = updateTime + timeInMilliseconds
            
            // Calculate time components
            val totalSeconds = elapsedTime / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            
            // Update time LiveData values
            _hours.value = String.format("%02d", hours)
            _minutes.value = String.format("%02d", minutes)
            _seconds.value = String.format("%02d", seconds)
            
            // Calculate and update decimal time
            val decimalValue = timeConversionRepository.standardToDecimalTime(
                hours.toInt(), minutes.toInt(), seconds.toInt()
            )
            _decimalTime.value = String.format("%.6f", decimalValue)
            
            // Schedule next update
            handler.postDelayed(this, 10)
        }
    }
    
    /**
     * Timer for background operations.
     */
    private var timer: Timer? = null
    
    /**
     * Starts the stopwatch.
     */
    fun startStopwatch() {
        if (!_isRunning.value!!) {
            startTime = SystemClock.uptimeMillis()
            handler.postDelayed(updateTimeRunnable, 0)
            _isRunning.value = true
        }
    }
    
    /**
     * Pauses the stopwatch.
     */
    fun pauseStopwatch() {
        if (_isRunning.value!!) {
            timeInMilliseconds += updateTime
            handler.removeCallbacks(updateTimeRunnable)
            _isRunning.value = false
        }
    }
    
    /**
     * Resets the stopwatch to zero.
     */
    fun resetStopwatch() {
        startTime = 0L
        timeInMilliseconds = 0L
        elapsedTime = 0L
        updateTime = 0L
        handler.removeCallbacks(updateTimeRunnable)
        
        // Reset display values
        _hours.value = "00"
        _minutes.value = "00"
        _seconds.value = "00"
        _decimalTime.value = "0.000"
        _isRunning.value = false
    }
    
    /**
     * Adds one minute to the current time.
     */
    fun addOneMinute() {
        timeInMilliseconds += 60 * 1000
        if (_isRunning.value!!) {
            // Force an immediate update
            handler.post(updateTimeRunnable)
        } else {
            // Update the display manually
            updateDisplayManually()
        }
    }
    
    /**
     * Adds one hour to the current time.
     */
    fun addOneHour() {
        timeInMilliseconds += 60 * 60 * 1000
        if (_isRunning.value!!) {
            // Force an immediate update
            handler.post(updateTimeRunnable)
        } else {
            // Update the display manually
            updateDisplayManually()
        }
    }
    
    /**
     * Updates display values when stopwatch is not running.
     */
    private fun updateDisplayManually() {
        // Calculate time components
        val totalSeconds = timeInMilliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        // Update time LiveData values
        _hours.value = String.format("%02d", hours)
        _minutes.value = String.format("%02d", minutes)
        _seconds.value = String.format("%02d", seconds)
        
        // Calculate and update decimal time
        val decimalValue = timeConversionRepository.standardToDecimalTime(
            hours.toInt(), minutes.toInt(), seconds.toInt()
        )
        _decimalTime.value = String.format("%.6f", decimalValue)
    }
    
    /**
     * Cleans up resources when ViewModel is being destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
    }
    
    /**
     * Factory for creating instances of this ViewModel.
     */
    class Factory(private val timeConversionRepository: TimeConversionRepository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StopwatchViewModel::class.java)) {
                return StopwatchViewModel(timeConversionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}