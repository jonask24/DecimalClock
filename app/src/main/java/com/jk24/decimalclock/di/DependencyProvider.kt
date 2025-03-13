package com.jk24.decimalclock.di

import android.content.Context
import com.jk24.decimalclock.data.repository.TimeConversionRepositoryImpl
import com.jk24.decimalclock.data.repository.TimePreferenceRepositoryImpl
import com.jk24.decimalclock.domain.repository.TimeConversionRepository
import com.jk24.decimalclock.domain.repository.TimePreferenceRepository
import com.jk24.decimalclock.domain.usecase.GetCurrentDateTimeUseCase
import com.jk24.decimalclock.domain.usecase.GetSelectedDateTimeUseCase
import com.jk24.decimalclock.domain.usecase.SaveSelectedDateTimeUseCase

/**
 * Dependency provider for manual dependency injection.
 * Centralizes object creation and provides singleton instances.
 */
object DependencyProvider {
    private var timePreferenceRepository: TimePreferenceRepository? = null
    private var timeConversionRepository: TimeConversionRepository? = null
    
    /**
     * Provides TimePreferenceRepository singleton instance.
     */
    fun provideTimePreferenceRepository(context: Context): TimePreferenceRepository {
        return timePreferenceRepository ?: synchronized(this) {
            TimePreferenceRepositoryImpl(context.applicationContext).also {
                timePreferenceRepository = it
            }
        }
    }
    
    /**
     * Provides TimeConversionRepository singleton instance.
     */
    fun provideTimeConversionRepository(): TimeConversionRepository {
        return timeConversionRepository ?: synchronized(this) {
            TimeConversionRepositoryImpl().also {
                timeConversionRepository = it
            }
        }
    }
    
    /**
     * Provides GetCurrentDateTimeUseCase instance.
     */
    fun provideGetCurrentDateTimeUseCase(context: Context): GetCurrentDateTimeUseCase {
        return GetCurrentDateTimeUseCase(provideTimeConversionRepository())
    }
    
    /**
     * Provides GetSelectedDateTimeUseCase instance.
     */
    fun provideGetSelectedDateTimeUseCase(context: Context): GetSelectedDateTimeUseCase {
        return GetSelectedDateTimeUseCase(provideTimePreferenceRepository(context))
    }
    
    /**
     * Provides SaveSelectedDateTimeUseCase instance.
     */
    fun provideSaveSelectedDateTimeUseCase(context: Context): SaveSelectedDateTimeUseCase {
        return SaveSelectedDateTimeUseCase(provideTimePreferenceRepository(context))
    }
}