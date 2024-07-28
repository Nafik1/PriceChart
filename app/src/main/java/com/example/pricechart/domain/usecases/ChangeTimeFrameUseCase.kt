package com.example.pricechart.domain.usecases

import com.example.pricechart.data.model.BarListDto
import com.example.pricechart.data.repository.PriceChartRepositoryImpl
import com.example.pricechart.domain.entity.BarListDo
import com.example.pricechart.presentation.LoadDataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangeTimeFrameUseCase @Inject constructor(
    private val repository : PriceChartRepositoryImpl
) {

    operator fun invoke(timeframe: String) : Flow<LoadDataState<BarListDo>> {
        return repository.getBarList(timeframe)
    }
}