package com.example.pricechart.presentation

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pricechart.R
import com.example.pricechart.domain.entity.BarDo
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun PriceChartScreen(
    viewModel: ChartViewModel
) {
    val screenState by viewModel.state.collectAsState()
    when {
        screenState.error != null -> TODO()

        screenState.isLoading -> ProgressBar()
        else -> PriceChart(barDos = screenState.barDoList, viewModel = viewModel, screennState = screenState)
    }
}

@Composable
private fun TimeFrames(
    selectedFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFrame.entries.forEach { timeFrame ->
            val labelResId = when(timeFrame) {
                TimeFrame.MIN_5 -> R.string.timeframe_5_minutes
                TimeFrame.MIN_15 -> R.string.timeframe_15_minutes
                TimeFrame.MIN_30 -> R.string.timeframe_30_minutes
                TimeFrame.HOUR_1 -> R.string.timeframe_1_hour
            }
            val isSelected = timeFrame == selectedFrame
            AssistChip(
                onClick = { onTimeFrameSelected(timeFrame) },
                label = { Text(text = stringResource(id = labelResId)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if(isSelected) Color.White else Color.Black,
                    labelColor = if(isSelected) Color.Black else Color.White
                )
            )
        }
    }
}

@Composable
private fun PriceChart(
    viewModel: ChartViewModel,
    modifier: Modifier = Modifier,
    barDos: List<BarDo>,
    screennState: ScreenState
) {
    
    Chart(
        modifier = modifier,
        screenState = screennState,
        timeFrame = screennState.timeFrame,
        onScreenStateChanged = { viewModel.updateState(it) }
    )

    barDos.firstOrNull()?.let {
        Prices(
            modifier = modifier,
            screenState = screennState,
            lastPrice = it.close
        )
    }
    
    TimeFrames(selectedFrame = screennState.timeFrame, onTimeFrameSelected = {viewModel.timeFrameSelected(it)})
}

@Composable
private fun Chart(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    onScreenStateChanged: (ScreenState) -> Unit,
    timeFrame: TimeFrame
) {
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (screenState.visibleBarsCount / zoomChange).roundToInt()
            .coerceIn(MIN_VISIBLE_BARS_COUNT, screenState.barDoList.size)

        val scrolledBy = (screenState.scrolledBy + panChange.x)
            .coerceIn(0f, screenState.barDoList.size * screenState.barWidth - screenState.screenWidth)

        onScreenStateChanged(
            screenState.copy(
                visibleBarsCount = visibleBarsCount,
                scrolledBy = scrolledBy
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(
                top = 32.dp,
                bottom = 32.dp,
                end = 32.dp
            )
            .transformable(transformableState)
            .onSizeChanged {
                onScreenStateChanged(
                    screenState.copy(
                        screenWidth = it.width.toFloat(),
                        screenHeight = it.height.toFloat()
                    )
                )
            }
    ) {
        val max = screenState.max
        val min = screenState.min
        val pxPerPoint = screenState.pxPerPoint
        translate(left = screenState.scrolledBy) {
            screenState.barDoList.forEachIndexed { index, bar ->
                val offsetX = size.width - (index * screenState.barWidth)
                drawTimeDelimiter(
                    barDto = bar,
                    nextBarDto = if(index < screenState.barDoList.size - 1) {
                        screenState.barDoList[index+1]
                    } else {
                        null
                    },
                    timeFrame = timeFrame,
                    offsetX = offsetX,
                    textMeasurer = textMeasurer
                )
                drawLine(
                    color = Color.White,
                    start = Offset(offsetX, size.height - ((bar.low - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.high - min) * pxPerPoint)),
                    strokeWidth = 1f
                )
                drawLine(
                    color = if (bar.open < bar.close) Color.Green else Color.Red,
                    start = Offset(offsetX, size.height - ((bar.open - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.close - min) * pxPerPoint)),
                    strokeWidth = screenState.barWidth / 2
                )
            }
        }
    }
}

@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    lastPrice: Float
) {
    val max = screenState.max
    val min = screenState.min
    val pxPerPoint = screenState.pxPerPoint
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(vertical = 32.dp)
    ) {
        drawPrices(max, min, pxPerPoint, lastPrice, textMeasurer)
    }
}

@SuppressLint("DefaultLocale", "SimpleDateFormat")
private fun DrawScope.drawTimeDelimiter(
    barDto: BarDo,
    nextBarDto: BarDo?,
    timeFrame: TimeFrame,
    offsetX: Float,
    textMeasurer: TextMeasurer
) {
    val calendar = barDto.calendar
    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val shouldDrawDelimiter = when(timeFrame) {
        TimeFrame.MIN_5 -> {
            minutes == 0
        }
        TimeFrame.MIN_15 -> {
            minutes == 0 && hours % 2 == 0
        }
        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            val nextBarDay = nextBarDto?.calendar?.get(Calendar.DAY_OF_MONTH)
            day != nextBarDay
        }
    }
    if(!shouldDrawDelimiter) return

    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(),4.dp.toPx())
        )
    )

    val nameOfMonth = SimpleDateFormat("MMM").format(calendar.get(Calendar.MONTH))
    val text = when(timeFrame) {
        TimeFrame.MIN_5, TimeFrame.MIN_15 -> {
            String.format("%02d:00",hours)
        }
        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            String.format("%s %s", day, nameOfMonth)
        }
    }
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
    )

}

private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    pxPerPoint: Float,
    lastPrice: Float,
    textMeasurer: TextMeasurer
) {
    val maxPriceOffsetY = 0f
    //max
    drawDashedLine(
        start = Offset(0f, maxPriceOffsetY),
        end = Offset(size.width, maxPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxPriceOffsetY
    )

    val lastPriceOffsetY = size.height - ((lastPrice - min) * pxPerPoint)
    //last price
    drawDashedLine(
        start = Offset(0f, lastPriceOffsetY),
        end = Offset(size.width, lastPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastPriceOffsetY
    )

    val minPriceOffsetY = size.height
    //min
    drawDashedLine(
        start = Offset(0f, minPriceOffsetY),
        end = Offset(size.width, minPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = minPriceOffsetY
    )
}

private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}