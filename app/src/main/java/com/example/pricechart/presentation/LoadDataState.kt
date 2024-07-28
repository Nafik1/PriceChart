package com.example.pricechart.presentation

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class LoadDataState<out T> {

    data class Success<T>(val data: T) : LoadDataState<T>()
    data class Error(val exception: Throwable) : LoadDataState<Nothing>()
    object Loading : LoadDataState<Nothing>()
}

fun <T> Flow<T>.asResult(): Flow<LoadDataState<T>> {
    return this
        .map<T, LoadDataState<T>> { LoadDataState.Success(it) }
        .onStart { emit(LoadDataState.Loading) }
        .catch { emit(LoadDataState.Error(it)) }
}