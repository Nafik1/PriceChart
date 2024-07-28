package com.example.pricechart.domain.entity

import android.icu.util.Calendar
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class BarDo(
    val open: Float,
    val close: Float,
    val low: Float,
    val high: Float,
    val time: Long
) : Parcelable {
    val calendar: Calendar
        get() {
            return Calendar.getInstance().apply {
                time = Date(this@BarDo.time)
            }
        }
}