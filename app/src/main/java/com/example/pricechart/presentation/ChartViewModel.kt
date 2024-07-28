package com.example.pricechart.presentation

import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricechart.domain.entity.BarListDo
import com.example.pricechart.domain.usecases.ChangeTimeFrameUseCase
import com.example.pricechart.domain.usecases.LoadBarListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val loadBarUseCase: LoadBarListUseCase,
    private val changeTimeFrameUseCase: ChangeTimeFrameUseCase
) : ViewModel() {


    private val _state = MutableStateFlow<ScreenState>(ScreenState())
    val state: StateFlow<ScreenState> = _state.asStateFlow()

    companion object {
        private const val LOG_TAG = "ChartViewModel"
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(LOG_TAG, "Exception caught: $throwable")
    }

    init {
        loadBarList()
    }

    private fun loadBarList() {
        viewModelScope.launch(exceptionHandler) {
            loadBarUseCase.invoke().collect {
                _state.value = it.mapToScreenState(state.value)
            }
        }
    }

    fun timeFrameSelected(timeFrame: TimeFrame) {
        _state.value = _state.value.copy(timeFrame = timeFrame)
        viewModelScope.launch {
            changeTimeFrameUseCase.invoke(timeFrame.value).collect {
                _state.value = it.mapToScreenState(state.value)
            }
        }
    }

    fun updateState(state: ScreenState) {
        _state.value = state
    }

    fun onRetryClick() {
        viewModelScope.launch {
            changeTimeFrameUseCase.invoke(_state.value.timeFrame.value).collect {
                _state.value = it.mapToScreenState(state.value)
            }
        }
    }


    private fun LoadDataState<BarListDo>.mapToScreenState(
        currentState: ScreenState
    ) = when (this) {
        is LoadDataState.Error -> {
            currentState.copy(
                isLoading = false,
                error = exception
            )
        }

        is LoadDataState.Loading -> {
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        is LoadDataState.Success -> {
            data.let { result ->
                currentState.copy(
                    isLoading = false,
                    error = null,
                    barDoList = result.barList
                )
            }
        }
    }
}