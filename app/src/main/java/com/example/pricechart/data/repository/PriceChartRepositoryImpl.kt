package com.example.pricechart.data.repository

import com.example.pricechart.data.network.ApiService
import com.example.pricechart.data.utils.Mapper
import com.example.pricechart.domain.entity.BarListDo
import com.example.pricechart.domain.repository.PriceChartRepository
import com.example.pricechart.presentation.LoadDataState
import com.example.pricechart.presentation.asResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PriceChartRepositoryImpl @Inject constructor(
    private val apiservice: ApiService,
    private val mapper: Mapper
) : PriceChartRepository {


    override fun getBarList(timeframe: String): Flow<LoadDataState<BarListDo>> {
        return flow {
            emit(apiservice.loadBars(timeframe))
        }.map { mapper.mapDtoToDo(it) }
            .asResult()
    }
}