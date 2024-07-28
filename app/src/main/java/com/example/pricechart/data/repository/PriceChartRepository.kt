package com.example.pricechart.data.repository

import com.example.pricechart.data.model.BarListDto
import com.example.pricechart.domain.entity.BarListDo
import com.example.pricechart.presentation.LoadDataState
import kotlinx.coroutines.flow.Flow

interface PriceChartRepository {

    fun getBarList(timeframe: String): Flow<LoadDataState<BarListDo>>
}