package com.meli.viewability.monitoring.data.entities

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class ComponentEntity (
    @DocumentId
    val id : String = "",
    var enabled : Boolean = true,
    var order : Int = 0,
    val type : String? = null,
    val size : String? = null,
    @get:PropertyName("business_data")
    @set:PropertyName("business_data")
    var businessData : Map<String, String> = mapOf(),
)