package com.jk24.decimalclock

import android.app.Application
import android.util.Log

/**
 * Application class for the DecimalClock app.
 * Handles global initialization.
 */
class DecimalClockApplication : Application() {
    companion object {
        private const val TAG = "DecimalClockApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application initialized")
        // Initialize any app-wide components here
    }
}