package com.meli.viewability.monitoring.di

import com.google.firebase.firestore.FirebaseFirestore
import com.meli.viewability.monitoring.data.source.remote.ViewabilityMonitoringRepositoryImpl
import com.meli.viewability.monitoring.domain.repository.ViewabilityMonitoringRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideViewabilityRepository(
        firestore: FirebaseFirestore
    ): ViewabilityMonitoringRepository {
        return ViewabilityMonitoringRepositoryImpl(firestore)
    }
}