package com.example.pricechart.data.utils

import com.example.pricechart.data.model.BarListDto
import com.example.pricechart.domain.entity.BarDo
import com.example.pricechart.domain.entity.BarListDo
import javax.inject.Inject

class Mapper @Inject constructor() {

    fun mapDtoToDo(barlistDto: BarListDto): BarListDo {
        val barlist = mutableListOf<BarDo>()

        for(bar in barlistDto.barDtoList) {
            val bardo = BarDo(
                open = bar.open,
                close = bar.close,
                low = bar.low,
                high = bar.high,
                time = bar.time
            )
            barlist.add(bardo)
        }
        return BarListDo(barlist)
    }
}