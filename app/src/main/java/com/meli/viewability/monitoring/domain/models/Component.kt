package com.meli.viewability.monitoring.domain.models

data class Component(
    val id : String = "",
    val size : ComponentSize? = null,
    val type : ComponentType? = null,
    val businessData : Map<String, String>,
)
