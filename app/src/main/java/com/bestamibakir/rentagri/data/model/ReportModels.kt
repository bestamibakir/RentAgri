package com.bestamibakir.rentagri.data.model

import java.util.Date

data class FinancialReport(
    val period: ReportPeriod,
    val startDate: Date,
    val endDate: Date,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val incomeRecords: List<FinancialRecord>,
    val expenseRecords: List<FinancialRecord>,
    val categoryBreakdown: Map<String, CategorySummary>,
    val dailyBreakdown: List<DailyFinancialSummary>,
    val hourlyBreakdown: List<HourlyTransactionSummary> = emptyList(),
    val incomeCategoriesHourly: Map<String, List<HourlyTransactionSummary>> = emptyMap(),
    val expenseCategoriesHourly: Map<String, List<HourlyTransactionSummary>> = emptyMap()
)

data class CategorySummary(
    val categoryName: String,
    val totalAmount: Double,
    val recordCount: Int,
    val percentage: Double,
    val isIncome: Boolean
)

data class DailyFinancialSummary(
    val date: Date,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val recordCount: Int
)

data class HourlyTransactionSummary(
    val hour: Int,
    val transactionCount: Int,
    val totalAmount: Double,
    val category: String = "",
    val isIncome: Boolean = true
)

enum class ReportPeriod(val displayName: String) {
    DAILY("Günlük"),
    WEEKLY("Haftalık"),
    MONTHLY("Aylık")
}

data class ChartData(
    val labels: List<String>,
    val incomeData: List<Float>,
    val expenseData: List<Float>,
    val balanceData: List<Float>
) 