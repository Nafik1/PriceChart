package com.example.pricechart.data.model

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

data class BarListDto(
   @SerializedName("results") val barDtoList: List<BarDto>
)
