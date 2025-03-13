package com.jk24.decimalclock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.jk24.decimalclock.R
import com.jk24.decimalclock.ui.stopwatch.StopwatchActivity
import java.util.Timer
import java.util.TimerTask

/**
 * Background service that handles stopwatch operation.
 * Maintains stopwatch state and timing even when the app is in background.
 */
class StopwatchService : Service() {
    /**
     * Binder given to clients for communication.
     */
    private val binder = StopwatchBinder()
    
    /**
     * Timer for periodic elapsed time calculations.
     */
    private var timer: Timer? = null
    
    /**
     * Indicates whether stopwatch is currently running.
     */
    private var isRunning = false
    
    /**
     * System time when stopwatch was started.
     */
    private var startTime: Long = 0
    
    /**
     * Total elapsed time in milliseconds.
     */
    private var elapsedTime: Long = 0
    
    /**
     * Constants for notification creation.
     */
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "StopwatchServiceChannel"
    }
    
    /**
     * Binder class for client communication with this service.
     */
    inner class StopwatchBinder : Binder() {
        /**
         * Returns service instance for direct method access.
         */
        fun getService(): StopwatchService = this@StopwatchService
    }
    
    /**
     * Called when the service is created.
     */
    override fun onCreate() {
        super.onCreate()
    }
    
    /**
     * Called when a client binds to the service.
     */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    /**
     * Called when the service is started.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // If the stopwatch is running, ensure foreground state
        if (isRunning) {
            startForeground(NOTIFICATION_ID, createNotification())
        }
        return START_STICKY
    }
    
    /**
     * Starts the stopwatch.
     */
    fun start() {
        if (!isRunning) {
            isRunning = true
            startTime = SystemClock.elapsedRealtime() - elapsedTime
            startTimer()
            
            // Move to foreground with notification
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }
    
    /**
     * Pauses the stopwatch.
     */
    fun pause() {
        isRunning = false
        timer?.cancel()
        timer = null
        
        // No longer need foreground priority
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    /**
     * Resets the stopwatch to zero.
     */
    fun reset() {
        isRunning = false
        timer?.cancel()
        timer = null
        elapsedTime = 0
        
        // No longer need foreground priority
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    /**
     * Gets current elapsed time.
     * 
     * @return Elapsed time in milliseconds
     */
    fun getElapsedTime(): Long {
        return if (isRunning) {
            SystemClock.elapsedRealtime() - startTime
        } else {
            elapsedTime
        }
    }
    
    /**
     * Checks if stopwatch is currently running.
     * 
     * @return True if running, false otherwise
     */
    fun isRunning(): Boolean {
        return isRunning
    }
    
    /**
     * Adds time to the current elapsed time.
     * 
     * @param timeToAdd Time in milliseconds to add
     */
    fun addTime(timeToAdd: Long) {
        elapsedTime += timeToAdd
        
        if (isRunning) {
            // Adjust start time to account for added time
            startTime = SystemClock.elapsedRealtime() - elapsedTime
        }
    }
    
    /**
     * Starts the timer for periodic updates.
     */
    private fun startTimer() {
        // Cancel any existing timer
        timer?.cancel()
        timer = Timer()
        
        scheduleNextUpdate()
    }
    
    /**
     * Schedules the next update task.
     */
    private fun scheduleNextUpdate() {
        if (!isRunning) return
        
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (isRunning) {
                    elapsedTime = SystemClock.elapsedRealtime() - startTime
                    
                    // Update notification periodically
                    if (elapsedTime % 1000 == 0L) { // Update once per second
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(NOTIFICATION_ID, createNotification())
                    }
                    
                    // Schedule the next execution with a new TimerTask
                    scheduleNextUpdate()
                }
            }
        }, 10) // 10ms delay
    }
    
    /**
     * Creates notification required for foreground service operation.
     * 
     * @return Properly configured notification
     */
    private fun createNotification(): Notification {
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Stopwatch Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        
        // Format time for notification display
        val seconds = elapsedTime / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val timeString = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
        
        // Create intent to return to stopwatch screen
        val intent = Intent(this, StopwatchActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build and return the notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Decimal Stopwatch")
            .setContentText("Running: $timeString")
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    /**
     * Cleans up when service is destroyed.
     */
    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }
}