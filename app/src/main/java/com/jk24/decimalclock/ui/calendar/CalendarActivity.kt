package com.jk24.decimalclock.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jk24.decimalclock.R
import com.jk24.decimalclock.di.DependencyProvider
import com.jk24.decimalclock.ui.base.BaseActivity
import com.jk24.decimalclock.ui.navigation.BottomNavigation
import java.util.Calendar

/**
 * Allows users to select dates and times and view their decimal representations.
 * Stores selections between app sessions.
 */
class CalendarActivity : BaseActivity() {
    private val TAG = "CalendarActivity"
    
    /**
     * ViewModel for this activity
     */
    private lateinit var viewModel: CalendarViewModel
    
    // Abstract property implementation
    override val bottomNavigationSelectedItemId: Int = R.id.nav_calendar
    
    // Abstract method implementation
    override fun getLayoutResourceId(): Int = R.layout.activity_calendar
    
    private lateinit var txtSelectedDateTime: TextView
    private lateinit var txtConvertedDateTime: TextView
    
    /**
     * Initializes data components and restores saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    /**
     * Sets up UI components and event listeners.
     */
    protected override fun initializeViews() {
        try {
            // Initialize ViewModel
            val timeConversionRepository = DependencyProvider.provideTimeConversionRepository()
            val getSelectedDateTimeUseCase = DependencyProvider.provideGetSelectedDateTimeUseCase(this)
            val saveSelectedDateTimeUseCase = DependencyProvider.provideSaveSelectedDateTimeUseCase(this)
            
            viewModel = ViewModelProvider(
                this, 
                CalendarViewModel.Factory(
                    timeConversionRepository,
                    getSelectedDateTimeUseCase,
                    saveSelectedDateTimeUseCase
                )
            ).get(CalendarViewModel::class.java)
            
            // Get view references
            val btnSelectDate = findViewById<Button>(R.id.btnSelectDate)
            val btnSelectTime = findViewById<Button>(R.id.btnSelectTime)
            txtSelectedDateTime = findViewById(R.id.txtSelectedDateTime)
            txtConvertedDateTime = findViewById(R.id.txtConvertedDateTime)
            
            // Observe ViewModel data
            viewModel.standardDateTime.observe(this) { dateTime ->
                txtSelectedDateTime.text = dateTime
            }
            
            viewModel.decimalDateTime.observe(this) { dateTime ->
                txtConvertedDateTime.text = dateTime
                txtConvertedDateTime.visibility = View.VISIBLE
            }
            
            viewModel.hasSelection.observe(this) { hasSelection ->
                if (!hasSelection) {
                    txtSelectedDateTime.text = ""
                    txtConvertedDateTime.text = ""
                    txtConvertedDateTime.visibility = View.INVISIBLE
                }
            }
            
            // Date picker dialog
            btnSelectDate.setOnClickListener { view ->
                Log.d(TAG, "Date button clicked")
                showDatePicker()
            }
            
            // Time picker dialog
            btnSelectTime.setOnClickListener { view ->
                Log.d(TAG, "Time button clicked")
                showTimePicker()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
        }
    }
    
    /**
     * Shows date picker dialog
     */
    private fun showDatePicker() {
        val calendar = viewModel.getCurrentCalendar()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                Log.d(TAG, "Date set: $year-$month-$dayOfMonth")
                viewModel.setSelectedDate(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    /**
     * Shows time picker dialog
     */
    private fun showTimePicker() {
        val calendar = viewModel.getCurrentCalendar()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                Log.d(TAG, "Time set: $hourOfDay:$minute")
                viewModel.setSelectedTime(hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true  // 24-hour format
        )
        timePickerDialog.show()
    }
    
    /**
     * Configures navigation components.
     */
    protected override fun setupBottomNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        BottomNavigation.setupBottomNavigation(this, bottomNavView, R.id.nav_calendar)
    }
    
    fun onSelectDateClick(view: View) {
        Log.d(TAG, "Date button clicked via XML onClick")
        showDatePicker()
    }

    fun onSelectTimeClick(view: View) {
        Log.d(TAG, "Time button clicked via XML onClick")
        showTimePicker()
    }
}