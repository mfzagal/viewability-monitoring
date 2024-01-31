package com.meli.viewability.monitoring.di

import com.meli.viewability.monitoring.domain.repository.ViewabilityMonitoringRepository
import com.meli.viewability.monitoring.domain.uses_case.GetComponentsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetComponentsUseCase(
        repository: ViewabilityMonitoringRepository,
    ): GetComponentsUseCase {
        return GetComponentsUseCase(repository)
    }

}