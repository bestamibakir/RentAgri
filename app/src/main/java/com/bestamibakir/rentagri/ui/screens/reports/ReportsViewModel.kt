package com.bestamibakir.rentagri.ui.screens.reports

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.*
import com.bestamibakir.rentagri.data.repository.ReportsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class ReportsState(
    val reports: List<FinancialReport> = emptyList(),
    val currentReport: FinancialReport? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    var reportsState by mutableStateOf(ReportsState())
        private set

    fun loadReports() {
        viewModelScope.launch {
            reportsState = reportsState.copy(isLoading = true, error = null)
            try {
                val reports = reportsRepository.getReports()
                val currentReport = reports.firstOrNull()

                reportsState = reportsState.copy(
                    reports = reports,
                    currentReport = currentReport,
                    isLoading = false
                )
            } catch (e: Exception) {
                reportsState = reportsState.copy(
                    isLoading = false,
                    error = "Raporlar yüklenirken bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun refreshReports() {
        reportsState = reportsState.copy(currentReport = null)
        loadReports()
    }

    fun refreshCurrentPeriod() {
        reportsState.currentReport?.let { report ->
            generateReportForPeriod(report.period)
        }
    }

    fun generateReportForPeriod(period: ReportPeriod, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!forceRefresh && reportsState.currentReport?.period == period) {
                return@launch
            }

            reportsState = reportsState.copy(isLoading = true, error = null)

            val result = reportsRepository.generateReport(period)

            result.fold(
                onSuccess = { report ->
                    reportsState = reportsState.copy(
                        isLoading = false,
                        currentReport = report,
                        error = null
                    )
                },
                onFailure = { error ->
                    reportsState = reportsState.copy(
                        isLoading = false,
                        error = error.message ?: "Rapor oluşturulurken hata oluştu"
                    )
                }
            )
        }
    }

    fun generateReport(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            reportsState = reportsState.copy(isLoading = true, error = null)

            val result = reportsRepository.generateReport(startDate, endDate)

            result.fold(
                onSuccess = { report ->
                    reportsState = reportsState.copy(
                        isLoading = false,
                        currentReport = report,
                        error = null
                    )
                },
                onFailure = { error ->
                    reportsState = reportsState.copy(
                        isLoading = false,
                        error = error.message ?: "Özel rapor oluşturulurken hata oluştu"
                    )
                }
            )
        }
    }

    fun deleteReport(reportId: String) {
        viewModelScope.launch {
            try {
                reportsRepository.deleteReport(reportId)
                loadReports()
            } catch (e: Exception) {
                reportsState = reportsState.copy(
                    error = "Rapor silinirken bir hata oluştu: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        reportsState = reportsState.copy(error = null)
    }
} 