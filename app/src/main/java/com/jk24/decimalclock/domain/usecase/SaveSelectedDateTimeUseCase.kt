package com.jk24.decimalclock.domain.usecase

import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.repository.TimePreferenceRepository

/**
 * Use case for saving user-selected date and time.
 * Encapsulates the business logic related to saving time preferences.
 */
class SaveSelectedDateTimeUseCase(private val timePreferenceRepository: TimePreferenceRepository) {
    /**
     * Saves the selected date time.
     * @param dateTime The DateTimeModel to save
     */
    fun execute(dateTime: DateTimeModel) {
        timePreferenceRepository.saveSelectedDateTime(dateTime)
    }
}