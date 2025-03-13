package com.jk24.decimalclock.domain.usecase

import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.repository.TimePreferenceRepository

/**
 * Use case for retrieving user-selected date and time.
 * Encapsulates the business logic related to retrieving time preferences.
 */
class GetSelectedDateTimeUseCase(private val timePreferenceRepository: TimePreferenceRepository) {
    /**
     * Gets the previously selected date time.
     * @return DateTimeModel or null if none saved
     */
    fun execute(): DateTimeModel? {
        return timePreferenceRepository.getSelectedDateTime()
    }
    
    /**
     * Checks if a date time selection exists.
     * @return true if selection exists, false otherwise
     */
    fun hasSelection(): Boolean {
        return timePreferenceRepository.hasSelectedDateTime()
    }
}