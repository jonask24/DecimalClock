package com.jk24.decimalclock.domain.usecase

import com.jk24.decimalclock.domain.model.DateTimeModel
import com.jk24.decimalclock.domain.repository.TimeConversionRepository

/**
 * Use case for getting the current date and time in various formats.
 * Encapsulates the business logic related to current time acquisition.
 */
class GetCurrentDateTimeUseCase(private val timeConversionRepository: TimeConversionRepository) {
    /**
     * Gets the current date and time.
     * @return DateTimeModel with current date and time information
     */
    fun execute(): DateTimeModel {
        return timeConversionRepository.getCurrentDateTime()
    }
}