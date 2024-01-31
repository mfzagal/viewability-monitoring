package com.meli.viewability.monitoring.domain.uses_case

import com.meli.viewability.monitoring.domain.repository.ViewabilityMonitoringRepository

class GetComponentsUseCase(private val servicesRepository: ViewabilityMonitoringRepository) {

    fun execute() = servicesRepository.getComponents()
}