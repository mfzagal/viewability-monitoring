package com.meli.viewability.monitoring.data.source.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.meli.viewability.monitoring.data.entities.ComponentEntity
import com.meli.viewability.monitoring.data.source.mapper.toComponent
import com.meli.viewability.monitoring.domain.models.Component
import com.meli.viewability.monitoring.domain.repository.ViewabilityMonitoringRepository
import com.meli.viewability.monitoring.domain.repository.response.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewabilityMonitoringRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore): ViewabilityMonitoringRepository {

    override fun getComponents(): Flow<ResultWrapper<List<Component>>> = flow {
        val components = mutableListOf<Component>()

        try {
            val result = firestore.collection("components")
                .orderBy("order")
                .whereEqualTo("enabled", true)
                .get()
                .await()

            result.documents.forEach { document ->
                document.toObject(ComponentEntity::class.java)?.toComponent()?.let {
                    components.add(it)
                }
            }

            emit(ResultWrapper.Success(components))
        } catch(ex: Exception) {
            Log.e("Services", "fail load components", ex)
            error("")
        }
    }
}