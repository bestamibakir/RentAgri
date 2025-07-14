package com.bestamibakir.rentagri.data.repository

import android.util.Log
import com.bestamibakir.rentagri.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsRepository @Inject constructor(
    private val financialRepository: FinancialRepository
) {

    private val tag = "ReportsRepository"

    suspend fun generateReport(period: ReportPeriod): Result<FinancialReport> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(tag, "Generating ${period.displayName} report")

                val (startDate, endDate) = getDateRangeForPeriod(period)
                Log.d(tag, "Date range for ${period.displayName}: $startDate to $endDate")

                val allRecords = financialRepository.getAllFinancialRecords()

                if (allRecords.isFailure) {
                    Log.e(
                        tag,
                        "Failed to get financial records: ${allRecords.exceptionOrNull()?.message}"
                    )
                    return@withContext Result.failure(
                        allRecords.exceptionOrNull() ?: Exception("Veriler alınamadı")
                    )
                }

                val records = allRecords.getOrNull() ?: emptyList()
                Log.d(tag, "Total financial records: ${records.size}")

                val filteredRecords = filterRecordsByDateRange(records, startDate, endDate)

                val incomeRecords = filteredRecords.filter { it.isIncome }
                val expenseRecords = filteredRecords.filter { !it.isIncome }

                val totalIncome = incomeRecords.sumOf { it.amount }
                val totalExpense = expenseRecords.sumOf { it.amount }
                val balance = totalIncome - totalExpense

                Log.d(
                    tag,
                    "${period.displayName} summary: Income=$totalIncome, Expense=$totalExpense, Balance=$balance"
                )
                Log.d(
                    tag,
                    "Income records: ${incomeRecords.size}, Expense records: ${expenseRecords.size}"
                )

                val categoryBreakdown =
                    generateCategoryBreakdown(filteredRecords, totalIncome, totalExpense)
                val dailyBreakdown = generateDailyBreakdown(filteredRecords, startDate, endDate)


                val hourlyBreakdown = if (period == ReportPeriod.DAILY) {
                    generateHourlyBreakdown(filteredRecords)
                } else emptyList()

                val incomeCategoriesHourly = if (period == ReportPeriod.DAILY) {
                    generateCategoryHourlyBreakdown(incomeRecords, true)
                } else emptyMap()

                val expenseCategoriesHourly = if (period == ReportPeriod.DAILY) {
                    generateCategoryHourlyBreakdown(expenseRecords, false)
                } else emptyMap()

                Log.d(tag, "Daily breakdown entries: ${dailyBreakdown.size}")
                Log.d(tag, "Category breakdown entries: ${categoryBreakdown.size}")
                Log.d(tag, "Hourly breakdown entries: ${hourlyBreakdown.size}")
                Log.d(tag, "Income categories hourly: ${incomeCategoriesHourly.size}")
                Log.d(tag, "Expense categories hourly: ${expenseCategoriesHourly.size}")

                val report = FinancialReport(
                    period = period,
                    startDate = startDate,
                    endDate = endDate,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = balance,
                    incomeRecords = incomeRecords,
                    expenseRecords = expenseRecords,
                    categoryBreakdown = categoryBreakdown,
                    dailyBreakdown = dailyBreakdown,
                    hourlyBreakdown = hourlyBreakdown,
                    incomeCategoriesHourly = incomeCategoriesHourly,
                    expenseCategoriesHourly = expenseCategoriesHourly
                )

                Log.d(
                    tag,
                    "Report generated successfully: ${period.displayName}, Balance: $balance"
                )
                Result.success(report)

            } catch (e: Exception) {
                Log.e(tag, "Error generating report for ${period.displayName}", e)
                Result.failure(e)
            }
        }

    private fun getDateRangeForPeriod(period: ReportPeriod): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        when (period) {
            ReportPeriod.DAILY -> {

                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endDate = calendar.time

                return Pair(startDate, endDate)
            }

            ReportPeriod.WEEKLY -> {

                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 23)
                today.set(Calendar.MINUTE, 59)
                today.set(Calendar.SECOND, 59)
                today.set(Calendar.MILLISECOND, 999)
                val endDate = today.time

                calendar.add(Calendar.DAY_OF_YEAR, -6)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                return Pair(startDate, endDate)
            }

            ReportPeriod.MONTHLY -> {

                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, 23)
                today.set(Calendar.MINUTE, 59)
                today.set(Calendar.SECOND, 59)
                today.set(Calendar.MILLISECOND, 999)
                val endDate = today.time

                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                return Pair(startDate, endDate)
            }
        }
    }

    private fun filterRecordsByDateRange(
        records: List<FinancialRecord>,
        startDate: Date,
        endDate: Date
    ): List<FinancialRecord> {
        Log.d(tag, "Filtering records between $startDate and $endDate")
        Log.d(tag, "Total records before filtering: ${records.size}")

        val filteredRecords = records.filter { record ->
            val recordTime = record.date.time
            val startTime = startDate.time
            val endTime = endDate.time

            recordTime >= startTime && recordTime <= endTime
        }

        Log.d(tag, "Filtered records count: ${filteredRecords.size}")


        if (filteredRecords.isNotEmpty()) {
            filteredRecords.take(3).forEach { record ->
                Log.d(tag, "Filtered record: ${record.title} - ${record.date}")
            }
        }

        return filteredRecords
    }

    private fun generateCategoryBreakdown(
        records: List<FinancialRecord>,
        totalIncome: Double,
        totalExpense: Double
    ): Map<String, CategorySummary> {
        val categoryMap = mutableMapOf<String, CategorySummary>()

        val incomeGroups = records.filter { it.isIncome }.groupBy { it.category }
        incomeGroups.forEach { (category, categoryRecords) ->
            val totalAmount = categoryRecords.sumOf { it.amount }
            val percentage = if (totalIncome > 0) (totalAmount / totalIncome) * 100 else 0.0

            categoryMap[category] = CategorySummary(
                categoryName = category,
                totalAmount = totalAmount,
                recordCount = categoryRecords.size,
                percentage = percentage,
                isIncome = true
            )
        }

        val expenseGroups = records.filter { !it.isIncome }.groupBy { it.category }
        expenseGroups.forEach { (category, categoryRecords) ->
            val totalAmount = categoryRecords.sumOf { it.amount }
            val percentage = if (totalExpense > 0) (totalAmount / totalExpense) * 100 else 0.0

            categoryMap[category] = CategorySummary(
                categoryName = category,
                totalAmount = totalAmount,
                recordCount = categoryRecords.size,
                percentage = percentage,
                isIncome = false
            )
        }

        return categoryMap
    }

    private fun generateDailyBreakdown(
        records: List<FinancialRecord>,
        startDate: Date,
        endDate: Date
    ): List<DailyFinancialSummary> {
        val dailyMap = mutableMapOf<String, MutableList<FinancialRecord>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


        records.forEach { record ->
            val dateKey = dateFormat.format(record.date)
            dailyMap.getOrPut(dateKey) { mutableListOf() }.add(record)
        }


        val dailySummaries = mutableListOf<DailyFinancialSummary>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time <= endDate) {
            val dateKey = dateFormat.format(calendar.time)
            val dayRecords = dailyMap[dateKey] ?: emptyList()

            val dayIncome = dayRecords.filter { it.isIncome }.sumOf { it.amount }
            val dayExpense = dayRecords.filter { !it.isIncome }.sumOf { it.amount }
            val dayBalance = dayIncome - dayExpense

            dailySummaries.add(
                DailyFinancialSummary(
                    date = calendar.time,
                    totalIncome = dayIncome,
                    totalExpense = dayExpense,
                    balance = dayBalance,
                    recordCount = dayRecords.size
                )
            )

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dailySummaries.sortedBy { it.date }
    }

    private fun generateHourlyBreakdown(records: List<FinancialRecord>): List<HourlyTransactionSummary> {
        val hourlyMap = mutableMapOf<Int, MutableList<FinancialRecord>>()


        records.forEach { record ->
            val calendar = Calendar.getInstance()
            calendar.time = record.date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hourlyMap.getOrPut(hour) { mutableListOf() }.add(record)
        }


        val hourlySummaries = mutableListOf<HourlyTransactionSummary>()
        for (hour in 0..23) {
            val hourRecords = hourlyMap[hour] ?: emptyList()
            val totalAmount = hourRecords.sumOf { it.amount }

            hourlySummaries.add(
                HourlyTransactionSummary(
                    hour = hour,
                    transactionCount = hourRecords.size,
                    totalAmount = totalAmount,
                    category = "",
                    isIncome = true
                )
            )
        }

        return hourlySummaries
    }

    private fun generateCategoryHourlyBreakdown(
        records: List<FinancialRecord>,
        isIncome: Boolean
    ): Map<String, List<HourlyTransactionSummary>> {
        val categoryHourlyMap = mutableMapOf<String, Map<Int, MutableList<FinancialRecord>>>()


        val categoryGroups = records.groupBy { it.category }

        categoryGroups.forEach { (category, categoryRecords) ->
            val hourlyMap = mutableMapOf<Int, MutableList<FinancialRecord>>()


            categoryRecords.forEach { record ->
                val calendar = Calendar.getInstance()
                calendar.time = record.date
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                hourlyMap.getOrPut(hour) { mutableListOf() }.add(record)
            }

            categoryHourlyMap[category] = hourlyMap
        }


        val result = mutableMapOf<String, List<HourlyTransactionSummary>>()

        categoryHourlyMap.forEach { (category, hourlyMap) ->
            val hourlySummaries = mutableListOf<HourlyTransactionSummary>()

            for (hour in 0..23) {
                val hourRecords = hourlyMap[hour] ?: emptyList()
                val totalAmount = hourRecords.sumOf { it.amount }

                hourlySummaries.add(
                    HourlyTransactionSummary(
                        hour = hour,
                        transactionCount = hourRecords.size,
                        totalAmount = totalAmount,
                        category = category,
                        isIncome = isIncome
                    )
                )
            }

            result[category] = hourlySummaries
        }

        return result
    }

    suspend fun getChartData(period: ReportPeriod): Result<ChartData> =
        withContext(Dispatchers.IO) {
            try {
                val report = generateReport(period)
                if (report.isFailure) {
                    return@withContext Result.failure(
                        report.exceptionOrNull() ?: Exception("Rapor oluşturulamadı")
                    )
                }

                val reportData = report.getOrNull()!!
                val dateFormat = when (period) {
                    ReportPeriod.DAILY -> SimpleDateFormat("HH:mm", Locale.getDefault())
                    ReportPeriod.WEEKLY -> SimpleDateFormat("dd/MM", Locale.getDefault())
                    ReportPeriod.MONTHLY -> SimpleDateFormat("dd", Locale.getDefault())
                }

                val labels = reportData.dailyBreakdown.map { dateFormat.format(it.date) }
                val incomeData = reportData.dailyBreakdown.map { it.totalIncome.toFloat() }
                val expenseData = reportData.dailyBreakdown.map { it.totalExpense.toFloat() }
                val balanceData = reportData.dailyBreakdown.map { it.balance.toFloat() }

                val chartData = ChartData(
                    labels = labels,
                    incomeData = incomeData,
                    expenseData = expenseData,
                    balanceData = balanceData
                )

                Result.success(chartData)

            } catch (e: Exception) {
                Log.e(tag, "Error generating chart data", e)
                Result.failure(e)
            }
        }

    suspend fun getReports(): List<FinancialReport> = withContext(Dispatchers.IO) {
        try {

            val reports = mutableListOf<FinancialReport>()

            ReportPeriod.values().forEach { period ->
                val reportResult = generateReport(period)
                reportResult.getOrNull()?.let { report ->
                    reports.add(report)
                }
            }

            Log.d(tag, "Generated ${reports.size} reports")
            reports
        } catch (e: Exception) {
            Log.e(tag, "Error getting reports", e)
            emptyList()
        }
    }

    suspend fun generateReport(startDate: Date, endDate: Date): Result<FinancialReport> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(tag, "Generating custom report from ${startDate} to ${endDate}")

                val allRecords = financialRepository.getAllFinancialRecords()

                if (allRecords.isFailure) {
                    return@withContext Result.failure(
                        allRecords.exceptionOrNull() ?: Exception("Veriler alınamadı")
                    )
                }

                val records = allRecords.getOrNull() ?: emptyList()
                val filteredRecords = filterRecordsByDateRange(records, startDate, endDate)

                val incomeRecords = filteredRecords.filter { it.isIncome }
                val expenseRecords = filteredRecords.filter { !it.isIncome }

                val totalIncome = incomeRecords.sumOf { it.amount }
                val totalExpense = expenseRecords.sumOf { it.amount }
                val balance = totalIncome - totalExpense

                val categoryBreakdown =
                    generateCategoryBreakdown(filteredRecords, totalIncome, totalExpense)
                val dailyBreakdown = generateDailyBreakdown(filteredRecords, startDate, endDate)


                val daysDiff = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                val period = when {
                    daysDiff == 1 -> ReportPeriod.DAILY
                    daysDiff <= 7 -> ReportPeriod.WEEKLY
                    else -> ReportPeriod.MONTHLY
                }


                val hourlyBreakdown = if (period == ReportPeriod.DAILY) {
                    generateHourlyBreakdown(filteredRecords)
                } else emptyList()

                val incomeCategoriesHourly = if (period == ReportPeriod.DAILY) {
                    generateCategoryHourlyBreakdown(incomeRecords, true)
                } else emptyMap()

                val expenseCategoriesHourly = if (period == ReportPeriod.DAILY) {
                    generateCategoryHourlyBreakdown(expenseRecords, false)
                } else emptyMap()

                val report = FinancialReport(
                    period = period,
                    startDate = startDate,
                    endDate = endDate,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balance = balance,
                    incomeRecords = incomeRecords,
                    expenseRecords = expenseRecords,
                    categoryBreakdown = categoryBreakdown,
                    dailyBreakdown = dailyBreakdown,
                    hourlyBreakdown = hourlyBreakdown,
                    incomeCategoriesHourly = incomeCategoriesHourly,
                    expenseCategoriesHourly = expenseCategoriesHourly
                )

                Log.d(tag, "Custom report generated: Balance: $balance")
                Result.success(report)

            } catch (e: Exception) {
                Log.e(tag, "Error generating custom report", e)
                Result.failure(e)
            }
        }

    suspend fun deleteReport(reportId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {

            Log.d(tag, "Delete report requested for ID: $reportId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error deleting report", e)
            Result.failure(e)
        }
    }
} 