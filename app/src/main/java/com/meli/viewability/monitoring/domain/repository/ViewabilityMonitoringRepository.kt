package com.meli.viewability.monitoring.domain.repository

import com.meli.viewability.monitoring.domain.models.Component
import com.meli.viewability.monitoring.domain.repository.response.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface ViewabilityMonitoringRepository {

    fun getComponents(): Flow<ResultWrapper<List<Component>>>

}