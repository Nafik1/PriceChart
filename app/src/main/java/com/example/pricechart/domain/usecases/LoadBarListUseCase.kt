package com.example.pricechart.domain.usecases

import com.example.pricechart.data.model.BarListDto
import com.example.pricechart.data.repository.PriceChartRepositoryImpl
import com.example.pricechart.domain.entity.BarListDo
import com.example.pricechart.presentation.LoadDataState
import com.example.pricechart.presentation.TimeFrame
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadBarListUseCase @Inject constructor(
    private val repository : PriceChartRepositoryImpl
) {

    operator fun invoke() : Flow<LoadDataState<BarListDo>> {
        return repository.getBarList(TimeFrame.HOUR_1.value)
    }
}