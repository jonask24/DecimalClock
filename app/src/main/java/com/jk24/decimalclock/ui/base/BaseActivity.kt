package com.jk24.decimalclock.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jk24.decimalclock.R
import com.jk24.decimalclock.ui.navigation.BottomNavigation

/**
 * Abstract base activity providing common functionality for all app activities.
 * Centralizes navigation setup and view initialization.
 */
abstract class BaseActivity : AppCompatActivity() {
    
    /**
     * Identifies which menu item to highlight in the bottom navigation.
     * Each child activity must define this.
     */
    abstract val bottomNavigationSelectedItemId: Int

    /**
     * Provides the layout resource ID to inflate.
     * Each child activity must define this.
     * @return The layout resource ID
     */
    abstract fun getLayoutResourceId(): Int
    
    /**
     * Sets up the activity with a consistent initialization flow.
     * @param savedInstanceState Contains data supplied to onSaveInstanceState()
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId()) 
        setupBottomNavigation() 
        initializeViews() 
    }
    
    /**
     * Sets up the bottom navigation with the correct selected item.
     * Child activities can override for custom navigation behavior.
     */
    protected open fun setupBottomNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavView?.let {
            BottomNavigation.setupBottomNavigation(this, it, bottomNavigationSelectedItemId)
        }
    }
    
    /**
     * Initialize activity-specific views.
     * Child activities should override to set up their UI elements.
     */
    protected open fun initializeViews() {
        // Default empty implementation
    }
    
    override fun onResume() {
        super.onResume()
        refreshNavigationSelection()
    }
    
    /**
     * Refreshes the navigation selection to ensure colors are updated correctly.
     * This is needed when returning to an activity from another.
     */
    protected fun refreshNavigationSelection() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavView?.let {
            // Make sure the correct item is selected and displayed with the active color
            it.selectedItemId = bottomNavigationSelectedItemId
            it.menu.findItem(bottomNavigationSelectedItemId)?.isChecked = true
        }
    }
    
    override fun onPause() {
        super.onPause()
    }
}