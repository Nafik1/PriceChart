package com.example.pricechart.presentation

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.example.pricechart.domain.entity.BarDo
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Stable
@Parcelize
data class ScreenState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val barDoList: List<BarDo> = listOf(),
    val visibleBarsCount: Int = 100,
    val screenWidth: Float = 1f,
    val screenHeight: Float = 1f,
    val scrolledBy: Float = 0f,
    val timeFrame: TimeFrame = TimeFrame.HOUR_1
) : Parcelable {
    val barWidth: Float
        get() = screenWidth / visibleBarsCount

    private val visibleBarDo: List<BarDo>
        get() {
            val endIndex =
                (scrolledBy / barWidth + visibleBarsCount).roundToInt().coerceAtMost(barDoList.size)
            val startIndex = (endIndex - visibleBarsCount).coerceAtLeast(0)
            return barDoList.subList(startIndex, endIndex)
        }

    val max: Float
        get() = visibleBarDo.maxOf { it.high }
    val min: Float
        get() = visibleBarDo.minOf { it.low }
    val pxPerPoint: Float
        get() = screenHeight / (max - min)
}
