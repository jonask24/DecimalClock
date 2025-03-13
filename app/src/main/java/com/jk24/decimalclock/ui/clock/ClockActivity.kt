package com.jk24.decimalclock.ui.clock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Space
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jk24.decimalclock.R
import com.jk24.decimalclock.ui.base.BaseActivity
import com.jk24.decimalclock.ui.calendar.CalendarActivity
import com.jk24.decimalclock.ui.navigation.BottomNavigation
import com.jk24.decimalclock.ui.stopwatch.StopwatchActivity
import com.jk24.decimalclock.di.DependencyProvider
import java.util.Date
import java.util.Timer

/**
 * Activity displaying standard and decimal clock representations.
 */
class ClockActivity : BaseActivity() {
   /**
    * ViewModel containing business logic and data handling.
    */
   private lateinit var viewModel: ClockViewModel
   
   /**
    * Timer reference for background tasks.
    */
   private var timer: Timer? = null  
   
   /**
    * Stores user-selected date/time when applicable.
    */
   private var selectedDateTime: Date? = null
   
   /**
    * Specifies navigation item selection.
    */
   override val bottomNavigationSelectedItemId: Int = R.id.nav_clock
   
   /**
    * Provides layout resource for inflation.
    */
   override fun getLayoutResourceId() = R.layout.activity_clock
   
   /**
    * Initializes views and sets up data observers.
    */
   override fun initializeViews() {
       val getCurrentDateTimeUseCase = DependencyProvider.provideGetCurrentDateTimeUseCase(this)
       viewModel = ViewModelProvider(this, ClockViewModel.Factory(getCurrentDateTimeUseCase))
           .get(ClockViewModel::class.java)
       
       // Set up observers for time and date displays
       viewModel.standardTime.observe(this) { time ->
           findViewById<TextView>(R.id.clockView).text = time
       }
       
       viewModel.decimalTime.observe(this) { time ->
           findViewById<TextView>(R.id.timeFractionField).text = time
       }
       
       viewModel.standardDate.observe(this) { date ->
           findViewById<TextView>(R.id.dateView).text = date
       }
       
       viewModel.decimalDate.observe(this) { date ->
           findViewById<TextView>(R.id.decimalDateView).text = date
       }
       
       viewModel.combinedDecimal.observe(this) { combined ->
           findViewById<TextView>(R.id.combinedDecimalView).text = combined
       }
       
       // Configure responsive spacing
       // val topSpacer = findViewById<Space>(R.id.topSpacer)
       // val displayMetrics = resources.displayMetrics
       // val screenHeight = displayMetrics.heightPixels
       // val spacerHeight = (screenHeight * 0.1).toInt() // 10% of screen height
       // topSpacer.layoutParams.height = spacerHeight
       
       // Initialize view references
       val timeLabel = findViewById<TextView>(R.id.timeLabel)
       val clockView = findViewById<TextView>(R.id.clockView)
       val timeFractionField = findViewById<TextView>(R.id.timeFractionField)
       val dateView = findViewById<TextView>(R.id.dateView)
       val decimalDateView = findViewById<TextView>(R.id.decimalDateView)
       val mixedView = findViewById<TextView>(R.id.mixedView)
       val combinedDecimalView = findViewById<TextView>(R.id.combinedDecimalView)
       
       setupBottomNavigation()
       
       viewModel.selectedDateTime.observe(this) { dateTime ->
           selectedDateTime = dateTime
       }
       
       viewModel.mixedDateTime.observe(this) { mixed ->
           findViewById<TextView>(R.id.mixedView).text = mixed
       }
   }
   
   /**
    * Standard creation lifecycle method.
    */
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       initializeViews()
   }
   
   /**
    * Called when activity becomes visible.
    */
   override fun onResume() {
       super.onResume()
       viewModel.startTimeUpdates()
   }
   
   /**
    * Called when activity is no longer visible.
    */
   override fun onPause() {
       super.onPause()
       viewModel.stopTimeUpdates()
       
       timer?.cancel()
       timer = null
   }
   
   /**
    * Called when activity is being destroyed.
    */
   override fun onDestroy() {
       super.onDestroy()
       timer?.cancel()
       timer = null
   }
   
   /**
    * Configures bottom navigation behavior.
    */
   protected override fun setupBottomNavigation() {
       try {
           val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
           BottomNavigation.setupBottomNavigation(this, bottomNavigation, R.id.nav_clock)
       } catch (e: Exception) {
           Log.e("ClockActivity", "Error setting up navigation: ${e.message}")
       }
   }
}