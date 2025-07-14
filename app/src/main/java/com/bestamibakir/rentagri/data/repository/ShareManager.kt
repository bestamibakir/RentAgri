package com.bestamibakir.rentagri.data.repository

import android.content.Context
import android.content.Intent
import com.bestamibakir.rentagri.data.model.FinancialReport
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun shareAsPdf(report: FinancialReport) {
        shareAsText(report, "PDF")
    }

    suspend fun shareAsExcel(report: FinancialReport) {
        shareAsText(report, "Excel")
    }

    suspend fun shareAsImage(report: FinancialReport) {
        shareAsText(report, "Görsel")
    }

    private fun shareAsText(report: FinancialReport, format: String) {
        val shareText = buildString {
            appendLine("Finansal Rapor ($format)")
            appendLine("================")
            appendLine("Dönem: ${report.period.displayName}")
            appendLine("Toplam Gelir: ₺${String.format("%.2f", report.totalIncome)}")
            appendLine("Toplam Gider: ₺${String.format("%.2f", report.totalExpense)}")
            appendLine("Net Bakiye: ₺${String.format("%.2f", report.balance)}")
            appendLine()
            appendLine("Kategori Dağılımı:")
            report.categoryBreakdown.forEach { (category, summary) ->
                appendLine(
                    "- $category: ₺${
                        String.format(
                            "%.2f",
                            summary.totalAmount
                        )
                    } (${String.format("%.1f", summary.percentage)}%)"
                )
            }
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Finansal Rapor")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val chooser = Intent.createChooser(shareIntent, "Raporu Paylaş").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(chooser)
    }
} 