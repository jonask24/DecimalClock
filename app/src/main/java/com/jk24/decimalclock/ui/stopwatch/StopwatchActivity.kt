package com.jk24.decimalclock.ui.stopwatch

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.Space
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jk24.decimalclock.R
import com.jk24.decimalclock.service.StopwatchService
import com.jk24.decimalclock.ui.base.BaseActivity
import com.jk24.decimalclock.ui.navigation.BottomNavigation
import com.jk24.decimalclock.di.DependencyProvider
import java.util.Timer

/**
 * Activity providing stopwatch functionality with standard and decimal time displays.
 * Uses a bound service to maintain stopwatch state across app navigation.
 */
class StopwatchActivity : BaseActivity() {

    /**
     * UI element references for time displays.
     */
    private lateinit var hoursDisplay: TextView
    private lateinit var minutesDisplay: TextView
    private lateinit var secondsDisplay: TextView
    private lateinit var decimalTimeDisplay: TextView
    private lateinit var decimalUnitDisplay: TextView  // New TextView for units
    
    /**
     * UI element references for control buttons.
     */
    private lateinit var btnStartStop: Button
    private lateinit var btnReset: Button
    private lateinit var btnAddMinute: Button
    private lateinit var btnAddHour: Button

    /**
     * Handler for scheduling UI updates.
     */
    private val handler = Handler(Looper.getMainLooper())
    
    /**
     * Service for background stopwatch operation.
     */
    private var stopwatchService: StopwatchService? = null
    
    /**
     * Tracks service connection state.
     */
    private var bound = false

    /**
     * Task for updating the UI at regular intervals.
     */
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (bound && stopwatchService != null) {
                updateStopwatchDisplay()
                handler.postDelayed(this, 16)
            }
        }
    }

    /**
     * Handles connection to stopwatch service.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StopwatchService.StopwatchBinder
            stopwatchService = binder.getService()
            bound = true

            updateButtonState()
            handler.removeCallbacks(updateRunnable) // Clear existing callbacks
            handler.post(updateRunnable) // Start UI updates
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            stopwatchService = null
            bound = false
            handler.removeCallbacks(updateRunnable)
        }
    }

    private lateinit var viewModel: StopwatchViewModel
    private var timer: Timer? = null  // Add this declaration

    /**
     * Request code for notification permission.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Start service regardless of permission result - notifications will only show if permitted
        startStopwatchService()
    }

    override val bottomNavigationSelectedItemId: Int = R.id.nav_stopwatch

    override fun getLayoutResourceId(): Int = R.layout.activity_stopwatch

    /**
     * Initializes views and sets up event listeners.
     */
    override fun initializeViews() {
        // Initialize view references
        hoursDisplay = findViewById(R.id.hoursDisplay)
        minutesDisplay = findViewById(R.id.minutesDisplay)
        secondsDisplay = findViewById(R.id.secondsDisplay)
        decimalTimeDisplay = findViewById(R.id.decimalTimeDisplay)
        decimalUnitDisplay = findViewById(R.id.decimalUnitDisplay)
        btnStartStop = findViewById(R.id.btnStartStop)
        btnReset = findViewById(R.id.btnReset)
        btnAddMinute = findViewById(R.id.btnAddMinute)
        btnAddHour = findViewById(R.id.btnAddHour)

        val timeConversionRepository = DependencyProvider.provideTimeConversionRepository()
        viewModel = ViewModelProvider(this, StopwatchViewModel.Factory(timeConversionRepository))
            .get(StopwatchViewModel::class.java)
        viewModel.isRunning.observe(this) { isRunning ->
            btnStartStop.text = if (isRunning) "STOP" else "START"
        }

        // Set up button click handlers
        btnStartStop.setOnClickListener {
            stopwatchService?.let { service ->
                if (service.isRunning()) {
                    service.pause()
                    btnStartStop.text = "START"
                } else {
                    service.start()
                    btnStartStop.text = "STOP"
                }
            }
        }

        btnReset.setOnClickListener {
            stopwatchService?.reset()
            updateStopwatchDisplay() // Force immediate UI update
        }

        btnAddMinute.setOnClickListener {
            addTimeToStopwatch(60 * 1000)
        }

        btnAddHour.setOnClickListener {
            addTimeToStopwatch(60 * 60 * 1000)
        }
    }

    /**
     * Starts and binds to the stopwatch service.
     */
    override fun onStart() {
        super.onStart()
        
        // Check and request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                    startStopwatchService()
                }
                else -> {
                    // Request the permission
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For versions below Android 13, no runtime permission needed
            startStopwatchService()
        }
    }

    /**
     * Starts and binds to the stopwatch service.
     */
    private fun startStopwatchService() {
        // Start the service to keep it running independently
        val serviceIntent = Intent(this, StopwatchService::class.java)
        startService(serviceIntent)
        
        // Bind to the service for UI updates
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Unbinds from the service when activity isn't visible.
     */
    override fun onStop() {
        super.onStop()
        if (bound) {
            // Remove update callback before unbinding
            handler.removeCallbacks(updateRunnable)
            unbindService(serviceConnection)
            bound = false
        }
        handler.removeCallbacks(updateRunnable)
    }

    /**
     * Updates the UI with current stopwatch values.
     */
    private fun updateStopwatchDisplay() {
        if (!bound || stopwatchService == null) return // Safety check
        
        val elapsedTime = stopwatchService!!.getElapsedTime()
        
        // Calculate hours, minutes, seconds
        val seconds = elapsedTime / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        // Update the individual displays
        hoursDisplay.text = String.format("%02d", hours % 100)
        minutesDisplay.text = String.format("%02d", minutes % 60)
        secondsDisplay.text = String.format("%02d", seconds % 60)
        
        // Calculate and display decimal time with unit prefixes
        updateDecimalTimeDisplay(elapsedTime)
    }
    
    /**
     * Formats and displays elapsed time in decimal format.
     */
    private fun updateDecimalTimeDisplay(elapsedMillis: Long) {
        // Convert to days (same base unit as other screens)
        val elapsedDays = elapsedMillis / (24.0 * 60 * 60 * 1000)
        
        // Calculate the appropriate prefix
        val prefixInfo = getDecimalPrefix(elapsedDays)
        
        decimalTimeDisplay.text = prefixInfo.formattedValue
        decimalUnitDisplay.text = prefixInfo.prefix
    }
    
    /**
     * Determines the appropriate decimal prefix based on time magnitude.
     */
    private fun getDecimalPrefix(value: Double): PrefixInfo {
        if (value == 0.0) {
            // Even for zero, show the milliday unit
            return PrefixInfo("0.000", "mD [milliday]")
        }
        
        return when {
            value >= 1 -> {
                // No prefix needed
                val formatted = formatWithPrecision(value)
                PrefixInfo(formatted, "D [day]")
            }
            value >= 0.1 -> {
                // Deci (d) - scale by 10
                val scaled = value * 10
                val formatted = formatWithPrecision(scaled)
                PrefixInfo(formatted, "dD [deciday]")
            }
            value >= 0.01 -> {
                // Centi (c) - scale by 100
                val scaled = value * 100
                val formatted = formatWithPrecision(scaled)
                PrefixInfo(formatted, "cD [centiday]")
            }
            value >= 0.001 -> {
                // Milli (m) - scale by 1000
                val scaled = value * 1000
                val formatted = formatWithPrecision(scaled)
                PrefixInfo(formatted, "mD [milliday]")
            }
            else -> {
                // Micro (μ) - scale by 1,000,000
                val scaled = value * 1000000
                val formatted = formatWithPrecision(scaled)
                PrefixInfo(formatted, "μD [microday]")
            }
        }
    }

    /**
     * Formats numeric values based on magnitude.
     */
    private fun formatWithPrecision(value: Double): String {
        // Format based on magnitude:
        // 1-9.99: show as X.XX
        // 10-99.9: show as XX.X
        // 100-999: show as XXX
        return when {
            value < 10 -> String.format("%.2f", value)
            value < 100 -> String.format("%.1f", value)
            else -> String.format("%.0f", value)
        }
    }
    
    /**
     * Data class for decimal value and its corresponding unit prefix.
     */
    private data class PrefixInfo(val formattedValue: String, val prefix: String)

    /**
     * Updates button states based on current stopwatch state.
     */
    private fun updateButtonState() {
        stopwatchService?.let { service ->
            // Update button text based on service state
            btnStartStop.text = if (service.isRunning()) "STOP" else "START"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }

    /**
     * Adds time to the stopwatch counter.
     */
    private fun addTimeToStopwatch(timeToAdd: Long) {
        stopwatchService?.let { service ->
            val currentTime = service.getElapsedTime()
            val wasRunning = service.isRunning()
            if (wasRunning) {
                service.pause()
            }
            service.addTime(timeToAdd)
            if (wasRunning) {
                service.start()
            }
            updateStopwatchDisplay()
        }
    }

    // Change the access modifier of setupBottomNavigation to protected
    protected override fun setupBottomNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavView?.let {
            BottomNavigation.setupBottomNavigation(this, it, R.id.nav_stopwatch)
        }
    }
}