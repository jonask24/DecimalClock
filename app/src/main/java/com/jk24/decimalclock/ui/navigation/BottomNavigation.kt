package com.jk24.decimalclock.ui.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.util.Log
import com.jk24.decimalclock.R
import com.jk24.decimalclock.ui.base.BaseActivity
import com.jk24.decimalclock.ui.calendar.CalendarActivity
import com.jk24.decimalclock.ui.clock.ClockActivity
import com.jk24.decimalclock.ui.stopwatch.StopwatchActivity

/**
 * Utility object handling bottom navigation setup and activity transitions.
 */
object BottomNavigation {
    private const val TAG = "BottomNavigation"
    
    /**
     * Sets up navigation with proper item selection and click handling.
     * 
     * @param activity The current activity context
     * @param bottomNavView The BottomNavigationView to configure
     * @param selectedItemId The ID of the item that should be selected
     */
    fun setupBottomNavigation(activity: BaseActivity, bottomNavView: BottomNavigationView, selectedItemId: Int) {
        // First, clear any previously checked items
        for (i in 0 until bottomNavView.menu.size()) {
            bottomNavView.menu.getItem(i).isChecked = false
        }
        
        // Set the selected item
        bottomNavView.selectedItemId = selectedItemId
        // Force a refresh of the selected item to ensure colors update
        bottomNavView.menu.findItem(selectedItemId)?.isChecked = true
        
        // Set up the item selection listener
        bottomNavView.setOnItemSelectedListener { item ->
            // Skip if already on the selected screen
            if (item.itemId == selectedItemId) {
                return@setOnItemSelectedListener true
            }
            
            // Log navigation actions
            Log.d(TAG, "Navigation selected: ${item.title}")
            
            // Create and launch appropriate intent
            try {
                val intent = when (item.itemId) {
                    R.id.nav_clock -> {
                        Intent(activity, ClockActivity::class.java)
                    }
                    R.id.nav_calendar -> {
                        Intent(activity, CalendarActivity::class.java)
                    }
                    R.id.nav_stopwatch -> {
                        Intent(activity, StopwatchActivity::class.java)
                    }
                    else -> null
                }
                
                intent?.let {
                    // Preserve activity state in the back stack
                    it.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    // Set the item as checked before starting the activity
                    item.isChecked = true
                    activity.startActivity(it)
                    Log.d(TAG, "Starting activity: ${it.component?.className}")
                }
                
                true
            } catch (e: Exception) {
                // Handle navigation errors
                Log.e(TAG, "Error navigating to activity", e)
                false
            }
        }
    }
    private fun navigateTo(activity: AppCompatActivity, activityClass: Class<*>) {
        val intent = Intent(activity, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        activity.startActivity(intent)
    }
}