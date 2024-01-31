package com.meli.viewability.monitoring.domain.repository.response

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class Error(val code: ResponseCode): ResultWrapper<Nothing>()
}