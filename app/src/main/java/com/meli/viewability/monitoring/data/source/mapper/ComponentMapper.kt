package com.meli.viewability.monitoring.data.source.mapper

import com.meli.viewability.monitoring.data.entities.ComponentEntity
import com.meli.viewability.monitoring.domain.models.Component
import com.meli.viewability.monitoring.domain.models.ComponentSize
import com.meli.viewability.monitoring.domain.models.ComponentType

fun Component.toComponentEntity() = ComponentEntity(
    id = this.id,
    businessData = this.businessData,
    size = this.size?.name,
    type = this.type?.name
)

fun ComponentEntity.toComponent() = Component(
    id = this.id,
    businessData = this.businessData,
    size = this.size?.let { ComponentSize.valueOf(it)},
    type = this.type?.let {ComponentType.valueOf(it)}
)

fun List<ComponentEntity>.toItemCarouselModelList() = mutableListOf<Component>().apply {
    this@toItemCarouselModelList.forEach { add(it.toComponent()) }
}