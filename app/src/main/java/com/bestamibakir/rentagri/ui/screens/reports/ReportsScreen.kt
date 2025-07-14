package com.bestamibakir.rentagri.ui.screens.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.data.model.*
import android.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import com.bestamibakir.rentagri.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.MONTHLY) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val reportsState = viewModel.reportsState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.generateReportForPeriod(selectedPeriod, forceRefresh = true)
    }

    LaunchedEffect(selectedPeriod) {
        viewModel.generateReportForPeriod(selectedPeriod, forceRefresh = true)
    }

    LaunchedEffect(reportsState.error) {
        reportsState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    Scaffold(
        topBar = {
            RentAgriTopAppBar(
                title = "Finansal Raporlar",
                onBackClick = onNavigateBack,
                actions = { }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        if (reportsState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EarthGreen60)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftCream)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    PeriodSelector(
                        selectedPeriod = selectedPeriod,
                        onPeriodSelected = { selectedPeriod = it }
                    )
                }


                item {
                    FinancialSummaryCards(reportsState.currentReport)
                }


                if (reportsState.currentReport != null) {

                    item {
                        if (selectedPeriod == ReportPeriod.DAILY) {
                            DailyCategoryChartsScrollable(reportsState.currentReport)
                        } else {
                            DailyTrendChart(reportsState.currentReport)
                        }
                    }


                    item {
                        RealCategoryAnalysis(reportsState.currentReport)
                    }


                    item {
                        ProfitabilityAnalysis(reportsState.currentReport)
                    }


                    item {
                        MonthlyComparisonChart(reportsState.currentReport)
                    }
                } else {

                    item {
                        NoDataCard()
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onApply = { startDate, endDate ->
                viewModel.generateReport(startDate, endDate)
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun NoDataCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = EarthGreen40.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Henüz finansal veri bulunmuyor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = EarthGreen40
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gelir-Gider Takibi bölümünden finansal kayıtlarınızı ekleyerek raporları görüntüleyebilirsiniz.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = EarthGreen40.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Rapor Dönemi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = EarthGreen40
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ReportPeriod.values()) { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { onPeriodSelected(period) },
                        label = {
                            Text(
                                period.displayName,
                                color = if (selectedPeriod == period) Color.White else EarthGreen40
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EarthGreen60,
                            containerColor = EarthGreen80.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialSummaryCards(report: FinancialReport?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Finansal Özet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = EarthGreen40
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryMetricCard(
                    title = "Toplam Gelir",
                    value = formatMoney(report?.totalIncome ?: 0.0),
                    icon = Icons.Default.ArrowUpward,
                    color = EarthGreen60,
                    modifier = Modifier.weight(1f)
                )

                SummaryMetricCard(
                    title = "Toplam Gider",
                    value = formatMoney(report?.totalExpense ?: 0.0),
                    icon = Icons.Default.ArrowDownward,
                    color = WarmBrown60,
                    modifier = Modifier.weight(1f)
                )

                SummaryMetricCard(
                    title = "Net Kâr",
                    value = formatMoney(report?.balance ?: 0.0),
                    icon = Icons.Default.AccountBalance,
                    color = if ((report?.balance
                            ?: 0.0) >= 0
                    ) EarthGreen60 else MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SummaryMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = EarthGreen40.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = EarthGreen40,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DailyTrendChart(report: FinancialReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getTrendChartTitle(report.period),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = EarthGreen40
                )

                if (report.period == ReportPeriod.DAILY) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LegendItem("Gelir İşlemi", EarthGreen60)
                        LegendItem("Gider İşlemi", WarmBrown60)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LegendItem("Gelir", EarthGreen60)
                        LegendItem("Gider", WarmBrown60)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val chartData = getChartDataForPeriod(report)

            if (chartData.isNotEmpty()) {

                when (report.period) {
                    ReportPeriod.DAILY -> {
                        DailyTransactionChart(
                            data = chartData,
                            report = report,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                    }

                    else -> {
                        SmartLineChart(
                            data = chartData,
                            period = report.period,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getEmptyDataMessage(report.period),
                        style = MaterialTheme.typography.bodyMedium,
                        color = EarthGreen40.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getXAxisLabel(report.period),
                    style = MaterialTheme.typography.bodySmall,
                    color = EarthGreen40.copy(alpha = 0.6f)
                )
                Text(
                    text = getYAxisLabel(report.period),
                    style = MaterialTheme.typography.bodySmall,
                    color = EarthGreen40.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getChartDescription(report.period),
                style = MaterialTheme.typography.bodySmall,
                color = EarthGreen40.copy(alpha = 0.6f)
            )
        }
    }
}

fun getTrendChartTitle(period: ReportPeriod): String {
    return when (period) {
        ReportPeriod.DAILY -> "Günlük İşlem Analizi"
        ReportPeriod.WEEKLY -> "Haftalık Tutar Trendi"
        ReportPeriod.MONTHLY -> "Aylık Tutar Trendi"
    }
}

fun getXAxisLabel(period: ReportPeriod): String {
    return when (period) {
        ReportPeriod.DAILY -> "Kategori/Saat"
        ReportPeriod.WEEKLY -> "Günler"
        ReportPeriod.MONTHLY -> "Günler/Haftalar"
    }
}

fun getYAxisLabel(period: ReportPeriod): String {
    return when (period) {
        ReportPeriod.DAILY -> "İşlem Sayısı"
        ReportPeriod.WEEKLY -> "Tutar (TL)"
        ReportPeriod.MONTHLY -> "Tutar (TL)"
    }
}

fun getEmptyDataMessage(period: ReportPeriod): String {
    return when (period) {
        ReportPeriod.DAILY -> "Bugün henüz işlem bulunmuyor.\nFinansal kayıtlarınızı ekleyerek analizi görüntüleyebilirsiniz."
        ReportPeriod.WEEKLY -> "Bu hafta için veri bulunmuyor."
        ReportPeriod.MONTHLY -> "Bu ay için veri bulunmuyor."
    }
}

fun getChartDescription(period: ReportPeriod): String {
    return when (period) {
        ReportPeriod.DAILY -> "Bugünkü gelir-gider işlemlerinin sayısı ve dağılımı"
        ReportPeriod.WEEKLY -> "Bu haftanın günlük gelir-gider tutar analizi"
        ReportPeriod.MONTHLY -> "Bu ayın gelir-gider tutar trend analizi"
    }
}

fun getChartDataForPeriod(report: FinancialReport): List<DailyFinancialSummary> {
    println("Getting chart data for ${report.period.displayName}, dailyBreakdown size: ${report.dailyBreakdown.size}")

    return when (report.period) {
        ReportPeriod.DAILY -> {
            if (report.dailyBreakdown.isNotEmpty()) {
                println("Daily report: showing ${report.dailyBreakdown.size} day(s)")
                report.dailyBreakdown
            } else {
                println("Daily report: no data available")
                emptyList()
            }
        }

        ReportPeriod.WEEKLY -> {
            println("Weekly report: showing ${report.dailyBreakdown.size} days")
            report.dailyBreakdown
        }

        ReportPeriod.MONTHLY -> {
            if (report.dailyBreakdown.size > 10) {
                println("Monthly report: generating weekly summaries from ${report.dailyBreakdown.size} days")
                generateWeeklySummaries(report.dailyBreakdown)
            } else {
                println("Monthly report: showing all ${report.dailyBreakdown.size} days")
                report.dailyBreakdown
            }
        }
    }
}


fun generateWeeklySummaries(dailyData: List<DailyFinancialSummary>): List<DailyFinancialSummary> {
    if (dailyData.isEmpty()) return emptyList()

    val weeklySummaries = mutableListOf<DailyFinancialSummary>()
    val calendar = Calendar.getInstance()

    val weekGroups = dailyData.groupBy { daily ->
        calendar.time = daily.date
        calendar.get(Calendar.WEEK_OF_YEAR)
    }

    weekGroups.values.forEach { weekData ->
        if (weekData.isNotEmpty()) {
            val weekStart = weekData.minByOrNull { it.date }?.date ?: Date()
            val totalIncome = weekData.sumOf { it.totalIncome }
            val totalExpense = weekData.sumOf { it.totalExpense }
            val totalRecords = weekData.sumOf { it.recordCount }

            weeklySummaries.add(
                DailyFinancialSummary(
                    date = weekStart,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = totalIncome - totalExpense,
                    recordCount = totalRecords
                )
            )
        }
    }

    return weeklySummaries.sortedBy { it.date }
}

@Composable
fun DailyCategoryChartsScrollable(report: FinancialReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Günlük İşlem Analizi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = EarthGreen40
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kategorilere göre saatlik işlem dağılımı",
                style = MaterialTheme.typography.bodySmall,
                color = EarthGreen40.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))


            val incomeCategories = report.incomeCategoriesHourly.map { (categoryName, hourlyData) ->
                CategoryHourlyData(
                    categoryName = categoryName,
                    isIncome = true,
                    hourlyData = hourlyData
                )
            }

            val expenseCategories =
                report.expenseCategoriesHourly.map { (categoryName, hourlyData) ->
                    CategoryHourlyData(
                        categoryName = categoryName,
                        isIncome = false,
                        hourlyData = hourlyData
                    )
                }


            val incomeCategoriesToShow = if (incomeCategories.isNotEmpty()) {
                incomeCategories
            } else {
                report.categoryBreakdown.values
                    .filter { it.isIncome && it.recordCount > 0 }
                    .map { category ->
                        CategoryHourlyData(
                            categoryName = category.categoryName,
                            isIncome = true,
                            hourlyData = generateMockHourlyData(
                                category.recordCount,
                                isIncome = true
                            )
                        )
                    }
            }

            val expenseCategoriesToShow = if (expenseCategories.isNotEmpty()) {
                expenseCategories
            } else {
                report.categoryBreakdown.values
                    .filter { !it.isIncome && it.recordCount > 0 }
                    .map { category ->
                        CategoryHourlyData(
                            categoryName = category.categoryName,
                            isIncome = false,
                            hourlyData = generateMockHourlyData(
                                category.recordCount,
                                isIncome = false
                            )
                        )
                    }
            }

            val allCategories = incomeCategoriesToShow + expenseCategoriesToShow

            if (allCategories.isNotEmpty()) {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(allCategories) { categoryData ->
                        CategoryTab(
                            categoryName = categoryData.categoryName,
                            isIncome = categoryData.isIncome,
                            recordCount = categoryData.hourlyData.sumOf { it.transactionCount }
                        )
                    }
                }


                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(allCategories) { categoryData ->
                        CategoryHourlyChart(
                            categoryData = categoryData,
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Saat (0-23)",
                        style = MaterialTheme.typography.bodySmall,
                        color = EarthGreen40.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "İşlem Sayısı",
                        style = MaterialTheme.typography.bodySmall,
                        color = EarthGreen40.copy(alpha = 0.6f)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bugün henüz işlem bulunmuyor.\nFinansal kayıtlarınızı ekleyerek analizi görüntüleyebilirsiniz.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EarthGreen40.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

data class CategoryHourlyData(
    val categoryName: String,
    val isIncome: Boolean,
    val hourlyData: List<HourlyTransactionSummary>
)

@Composable
fun CategoryTab(
    categoryName: String,
    isIncome: Boolean,
    recordCount: Int
) {
    val backgroundColor =
        if (isIncome) EarthGreen80.copy(alpha = 0.2f) else WarmBrown80.copy(alpha = 0.2f)
    val textColor = if (isIncome) EarthGreen60 else WarmBrown60

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = getCategoryIcon(categoryName),
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = textColor
            )
            Text(
                text = "($recordCount)",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CategoryHourlyChart(
    categoryData: CategoryHourlyData,
    modifier: Modifier = Modifier
) {
    val chartColor = if (categoryData.isIncome) EarthGreen60 else WarmBrown60
    val maxTransactions = categoryData.hourlyData.maxOfOrNull { it.transactionCount } ?: 1

    Column(
        modifier = modifier
    ) {

        Text(
            text = categoryData.categoryName,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = EarthGreen40,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val padding = 30.dp.toPx()
            val chartWidth = size.width - padding * 2
            val chartHeight = size.height - padding * 2


            val hoursToShow = listOf(0, 6, 12, 18, 23)
            hoursToShow.forEach { hour ->
                val x = padding + (hour / 23f) * chartWidth


                drawLine(
                    color = Color.Gray.copy(alpha = 0.1f),
                    start = Offset(x, padding),
                    end = Offset(x, padding + chartHeight),
                    strokeWidth = 1.dp.toPx()
                )


                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = android.graphics.Color.parseColor("#4E7C3B")
                        textSize = 20f
                        textAlign = Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText(
                        "$hour:00",
                        x,
                        padding + chartHeight + 20.dp.toPx(),
                        paint
                    )
                }
            }


            val yGridLines = 3
            for (i in 1..yGridLines) {
                val y = padding + (chartHeight / yGridLines) * i
                drawLine(
                    color = Color.Gray.copy(alpha = 0.1f),
                    start = Offset(padding, y),
                    end = Offset(padding + chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )


                val value = ((yGridLines - i + 1) * maxTransactions / yGridLines)
                if (value > 0) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.parseColor("#4E7C3B")
                            textSize = 18f
                            textAlign = Paint.Align.RIGHT
                        }
                        canvas.nativeCanvas.drawText(
                            value.toString(),
                            padding - 8.dp.toPx(),
                            y + 5.dp.toPx(),
                            paint
                        )
                    }
                }
            }


            val barWidth = chartWidth / 24 * 0.6f
            categoryData.hourlyData.forEach { hourlyData ->
                if (hourlyData.transactionCount > 0) {
                    val x = padding + (hourlyData.hour / 23f) * chartWidth
                    val barHeight =
                        (hourlyData.transactionCount.toFloat() / maxTransactions) * chartHeight
                    val y = padding + chartHeight - barHeight

                    drawRoundRect(
                        color = chartColor,
                        topLeft = Offset(x - barWidth / 2, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                    )


                    if (hourlyData.transactionCount > 0) {
                        drawIntoCanvas { canvas ->
                            val paint = Paint().apply {
                                color = chartColor.toArgb()
                                textSize = 16f
                                textAlign = Paint.Align.CENTER
                                isFakeBoldText = true
                            }
                            canvas.nativeCanvas.drawText(
                                hourlyData.transactionCount.toString(),
                                x,
                                y - 5.dp.toPx(),
                                paint
                            )
                        }
                    }
                }
            }


            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(padding, padding + chartHeight),
                end = Offset(padding + chartWidth, padding + chartHeight),
                strokeWidth = 2.dp.toPx()
            )

            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(padding, padding),
                end = Offset(padding, padding + chartHeight),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}


fun generateMockHourlyData(totalRecords: Int, isIncome: Boolean): List<HourlyTransactionSummary> {
    val hours = (0..23).toList()
    val data = mutableListOf<HourlyTransactionSummary>()


    val activeHours = when {
        totalRecords == 1 -> listOf((8..18).random())
        totalRecords <= 3 -> (8..18).shuffled().take(totalRecords)
        else -> (6..20).shuffled().take((totalRecords * 0.7).toInt().coerceAtLeast(1))
    }

    hours.forEach { hour ->
        val transactionCount = if (hour in activeHours) {
            when {
                totalRecords == 1 -> 1
                totalRecords <= 3 -> 1
                else -> (1..3).random()
            }
        } else 0

        data.add(
            HourlyTransactionSummary(
                hour = hour,
                transactionCount = transactionCount,
                totalAmount = if (transactionCount > 0) (100..5000).random().toDouble() else 0.0,
                category = "",
                isIncome = isIncome
            )
        )
    }

    return data
}

@Composable
fun DailyTransactionChart(
    data: List<DailyFinancialSummary>,
    report: FinancialReport,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return


    val incomeTransactions = report.incomeRecords.size.toFloat()
    val expenseTransactions = report.expenseRecords.size.toFloat()
    val maxCount = maxOf(incomeTransactions, expenseTransactions, 1f)

    Canvas(modifier = modifier) {
        val padding = 50.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        if (maxCount > 0) {

            val barWidth = chartWidth / 5
            val barSpacing = barWidth / 2


            val incomeHeight = (incomeTransactions / maxCount * chartHeight)
            drawRoundRect(
                color = Color(0xFF8BC34A),
                topLeft = Offset(padding + barSpacing, padding + chartHeight - incomeHeight),
                size = Size(barWidth, incomeHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
            )


            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = android.graphics.Color.parseColor("#4E7C3B")
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(
                    "Gelir\n${incomeTransactions.toInt()}",
                    padding + barSpacing + barWidth / 2,
                    padding + chartHeight + 20.dp.toPx(),
                    paint
                )
            }


            val expenseHeight = (expenseTransactions / maxCount * chartHeight)
            drawRoundRect(
                color = Color(0xFFA1887F),
                topLeft = Offset(
                    padding + barWidth * 2 + barSpacing,
                    padding + chartHeight - expenseHeight
                ),
                size = Size(barWidth, expenseHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
            )


            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = android.graphics.Color.parseColor("#8D6E63")
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(
                    "Gider\n${expenseTransactions.toInt()}",
                    padding + barWidth * 2 + barSpacing + barWidth / 2,
                    padding + chartHeight + 20.dp.toPx(),
                    paint
                )
            }


            val gridLines = 4
            for (i in 1..gridLines) {
                val y = padding + (chartHeight / gridLines) * i
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(padding, y),
                    end = Offset(padding + chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )


                val value = ((gridLines - i + 1) * maxCount / gridLines).toInt()
                if (value > 0) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.parseColor("#4E7C3B")
                            textSize = 24f
                            textAlign = Paint.Align.RIGHT
                        }
                        canvas.nativeCanvas.drawText(
                            value.toString(),
                            padding - 10.dp.toPx(),
                            y + 5.dp.toPx(),
                            paint
                        )
                    }
                }
            }
        }


        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(padding, padding + chartHeight),
            end = Offset(padding + chartWidth, padding + chartHeight),
            strokeWidth = 2.dp.toPx()
        )

        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(padding, padding),
            end = Offset(padding, padding + chartHeight),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun SmartLineChart(
    data: List<DailyFinancialSummary>,
    period: ReportPeriod,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Canvas(modifier = modifier) {
        val padding = 50.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        val maxValue = data.maxOfOrNull { maxOf(it.totalIncome, it.totalExpense) } ?: 1.0
        if (maxValue == 0.0) return@Canvas

        val stepX = if (data.size > 1) chartWidth / (data.size - 1) else chartWidth / 2


        val gridLines = 4
        for (i in 1..gridLines) {
            val y = padding + (chartHeight / gridLines) * i
            drawLine(
                color = Color.Gray.copy(alpha = 0.1f),
                start = Offset(padding, y),
                end = Offset(padding + chartWidth, y),
                strokeWidth = 1.dp.toPx()
            )


            val value = ((gridLines - i + 1) * maxValue / gridLines)
            if (value > 0) {
                val formattedValue = if (value >= 1000) {
                    "${(value / 1000).toInt()}K"
                } else {
                    value.toInt().toString()
                }

                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = android.graphics.Color.parseColor("#4E7C3B")
                        textSize = 20f
                        textAlign = Paint.Align.RIGHT
                    }
                    canvas.nativeCanvas.drawText(
                        formattedValue,
                        padding - 10.dp.toPx(),
                        y + 5.dp.toPx(),
                        paint
                    )
                }
            }
        }


        val lineStrokeWidth = when (period) {
            ReportPeriod.DAILY -> 2.dp.toPx()
            ReportPeriod.WEEKLY -> 3.dp.toPx()
            ReportPeriod.MONTHLY -> 2.5.dp.toPx()
        }


        val pointRadius = when (period) {
            ReportPeriod.DAILY -> 3.dp.toPx()
            ReportPeriod.WEEKLY -> 4.dp.toPx()
            ReportPeriod.MONTHLY -> 3.5.dp.toPx()
        }


        if (data.size > 1) {
            for (i in 0 until data.size - 1) {
                val currentPoint = Offset(
                    x = padding + i * stepX,
                    y = padding + chartHeight - (data[i].totalIncome / maxValue * chartHeight).toFloat()
                )
                val nextPoint = Offset(
                    x = padding + (i + 1) * stepX,
                    y = padding + chartHeight - (data[i + 1].totalIncome / maxValue * chartHeight).toFloat()
                )

                drawLine(
                    color = Color(0xFF8BC34A),
                    start = currentPoint,
                    end = nextPoint,
                    strokeWidth = lineStrokeWidth
                )
            }
        }


        if (data.size > 1) {
            for (i in 0 until data.size - 1) {
                val currentPoint = Offset(
                    x = padding + i * stepX,
                    y = padding + chartHeight - (data[i].totalExpense / maxValue * chartHeight).toFloat()
                )
                val nextPoint = Offset(
                    x = padding + (i + 1) * stepX,
                    y = padding + chartHeight - (data[i + 1].totalExpense / maxValue * chartHeight).toFloat()
                )

                drawLine(
                    color = Color(0xFFA1887F),
                    start = currentPoint,
                    end = nextPoint,
                    strokeWidth = lineStrokeWidth
                )
            }
        }


        data.forEachIndexed { index, summary ->
            val xPosition = if (data.size == 1) {

                padding + chartWidth / 2
            } else {
                padding + index * stepX
            }

            val incomePoint = Offset(
                x = xPosition,
                y = padding + chartHeight - (summary.totalIncome / maxValue * chartHeight).toFloat()
            )
            val expensePoint = Offset(
                x = xPosition,
                y = padding + chartHeight - (summary.totalExpense / maxValue * chartHeight).toFloat()
            )


            if (data.size == 1) {
                drawCircle(
                    color = Color(0xFF8BC34A).copy(alpha = 0.15f),
                    radius = pointRadius * 3,
                    center = incomePoint
                )
                drawCircle(
                    color = Color(0xFFA1887F).copy(alpha = 0.15f),
                    radius = pointRadius * 3,
                    center = expensePoint
                )

                drawCircle(
                    color = Color(0xFF8BC34A).copy(alpha = 0.3f),
                    radius = pointRadius * 2,
                    center = incomePoint
                )
                drawCircle(
                    color = Color(0xFFA1887F).copy(alpha = 0.3f),
                    radius = pointRadius * 2,
                    center = expensePoint
                )
            }


            drawCircle(
                color = Color(0xFF8BC34A),
                radius = if (data.size == 1) pointRadius * 1.5f else pointRadius,
                center = incomePoint
            )

            drawCircle(
                color = Color(0xFFA1887F),
                radius = if (data.size == 1) pointRadius * 1.5f else pointRadius,
                center = expensePoint
            )
        }


        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(padding, padding + chartHeight),
            end = Offset(padding + chartWidth, padding + chartHeight),
            strokeWidth = 2.dp.toPx()
        )

        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(padding, padding),
            end = Offset(padding, padding + chartHeight),
            strokeWidth = 2.dp.toPx()
        )


        if (data.size > 1) {
            val dateFormat = SimpleDateFormat(
                when (period) {
                    ReportPeriod.WEEKLY -> "E"
                    ReportPeriod.MONTHLY -> "dd"
                    else -> "HH:mm"
                }, Locale("tr", "TR")
            )


            val indicesToShow = when {
                data.size <= 3 -> data.indices.toList()
                data.size <= 7 -> listOf(0, data.size / 2, data.size - 1)
                else -> listOf(0, data.size / 3, 2 * data.size / 3, data.size - 1)
            }

            indicesToShow.forEach { index ->
                if (index < data.size) {
                    val xPosition = padding + index * stepX
                    val dateText = dateFormat.format(data[index].date)

                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.parseColor("#4E7C3B")
                            textSize = 18f
                            textAlign = Paint.Align.CENTER
                        }
                        canvas.nativeCanvas.drawText(
                            dateText,
                            xPosition,
                            padding + chartHeight + 25.dp.toPx(),
                            paint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RealCategoryAnalysis(report: FinancialReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Kategori Analizi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = EarthGreen40
            )

            Spacer(modifier = Modifier.height(16.dp))


            val incomeCategories = report.categoryBreakdown.values
                .filter { it.isIncome && it.totalAmount > 0 }
                .sortedByDescending { it.totalAmount }
                .take(3)


            val expenseCategories = report.categoryBreakdown.values
                .filter { !it.isIncome && it.totalAmount > 0 }
                .sortedByDescending { it.totalAmount }
                .take(3)

            if (incomeCategories.isNotEmpty() || expenseCategories.isNotEmpty()) {

                if (incomeCategories.isNotEmpty()) {
                    Text(
                        text = "Gelir Kategorileri",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = EarthGreen60,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    incomeCategories.forEach { category ->
                        RealCategoryItem(category, isIncomeCategory = true)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (expenseCategories.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }


                if (expenseCategories.isNotEmpty()) {
                    Text(
                        text = "Gider Kategorileri",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = WarmBrown60,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    expenseCategories.forEach { category ->
                        RealCategoryItem(category, isIncomeCategory = false)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bu dönemde kategori verisi bulunmuyor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EarthGreen40.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun RealCategoryItem(category: CategorySummary, isIncomeCategory: Boolean = false) {
    val backgroundColor =
        if (isIncomeCategory) EarthGreen80.copy(alpha = 0.2f) else WarmBrown80.copy(alpha = 0.2f)
    val iconColor = if (isIncomeCategory) EarthGreen60 else WarmBrown60
    val percentageColor = if (isIncomeCategory) EarthGreen60 else WarmBrown60

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = getCategoryIcon(category.categoryName),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = EarthGreen40
            )
            Text(
                text = "${formatMoney(category.totalAmount)} • ${category.recordCount} kayıt",
                style = MaterialTheme.typography.bodySmall,
                color = EarthGreen40.copy(alpha = 0.6f)
            )
        }

        Text(
            text = "${String.format("%.1f", category.percentage)}%",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = percentageColor
        )
    }
}

fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName.lowercase()) {
        "tohum" -> Icons.Default.Grass
        "gübre" -> Icons.Default.EmojiNature
        "yakıt" -> Icons.Default.LocalGasStation
        "ekipman" -> Icons.Default.Agriculture
        "işçilik" -> Icons.Default.Person
        "kira gideri" -> Icons.Default.Home
        "ürün satışı" -> Icons.Default.ShoppingCart
        "kira geliri" -> Icons.Default.AccountBalance
        "devlet desteği" -> Icons.Default.AccountBalanceWallet
        else -> Icons.Default.Category
    }
}

@Composable
fun MonthlyComparisonChart(report: FinancialReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gelir vs Gider",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = EarthGreen40
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LegendItem("Gelir", EarthGreen60)
                    LegendItem("Gider", WarmBrown60)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            SimpleComparisonBars(
                income = report.totalIncome,
                expense = report.totalExpense,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${report.period.displayName} dönemi karşılaştırması",
                style = MaterialTheme.typography.bodySmall,
                color = EarthGreen40.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SimpleComparisonBars(
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    val maxValue = maxOf(income, expense).coerceAtLeast(1.0)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gelir",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EarthGreen40
                )
                Text(
                    text = formatMoney(income),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = EarthGreen60
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(EarthGreen80.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((income / maxValue).toFloat())
                        .clip(RoundedCornerShape(6.dp))
                        .background(EarthGreen60)
                )
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gider",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EarthGreen40
                )
                Text(
                    text = formatMoney(expense),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = WarmBrown60
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(WarmBrown80.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((expense / maxValue).toFloat())
                        .clip(RoundedCornerShape(6.dp))
                        .background(WarmBrown60)
                )
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = EarthGreen40
        )
    }
}

@Composable
fun ProfitabilityAnalysis(report: FinancialReport) {
    val profitMargin = if (report.totalIncome > 0) {
        (report.balance / report.totalIncome * 100).coerceIn(0.0, 100.0)
    } else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Kârlılık Analizi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = EarthGreen40
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressChart(
                    percentage = profitMargin.toFloat(),
                    size = 120.dp,
                    strokeWidth = 12.dp,
                    color = if (profitMargin >= 20) EarthGreen60 else if (profitMargin >= 10) GoldenYellow60 else WarmBrown60
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfitMetric(
                    title = "Kâr Marjı",
                    value = "${profitMargin.toInt()}%",
                    color = EarthGreen60
                )
                ProfitMetric(
                    title = "Net Gelir",
                    value = formatMoney(report.balance),
                    color = if (report.balance >= 0) EarthGreen60 else MaterialTheme.colorScheme.error
                )
                ProfitMetric(
                    title = "Durum",
                    value = if (profitMargin >= 15) "İyi" else if (profitMargin >= 5) "Orta" else "Dikkat",
                    color = if (profitMargin >= 15) EarthGreen60 else if (profitMargin >= 5) GoldenYellow60 else WarmBrown60
                )
            }
        }
    }
}

@Composable
fun CircularProgressChart(
    percentage: Float,
    size: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp,
    color: Color
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (size.toPx() - strokeWidthPx) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)


            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidthPx)
            )


            val sweepAngle = (animatedPercentage / 100f) * 360f
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidthPx,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                ),
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(size.toPx() - strokeWidthPx, size.toPx() - strokeWidthPx)
            )
        }

        Text(
            text = "${animatedPercentage.toInt()}%",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = EarthGreen40
        )
    }
}

@Composable
fun ProfitMetric(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = EarthGreen40.copy(alpha = 0.7f)
        )
    }
}


fun formatMoney(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onApply: (Date, Date) -> Unit
) {
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tarih Aralığı Seçin") },
        text = {
            Column {
                Text(
                    "Başlangıç: ${
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                            startDate
                        )
                    }"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Bitiş: ${
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(endDate)
                    }"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(startDate, endDate) }) {
                Text("Uygula")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    RentAgriTheme {
        ReportsScreen(onNavigateBack = {})
    }
}