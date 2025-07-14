package com.bestamibakir.rentagri.ui.screens.financial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.FinancialRecord
import com.bestamibakir.rentagri.data.repository.CategoryRepository
import com.bestamibakir.rentagri.data.repository.FinancialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class FinancialState(
    val records: List<FinancialRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val incomeCategories: List<String> = emptyList(),
    val expenseCategories: List<String> = emptyList(),
    val editingRecord: FinancialRecord? = null
)

@HiltViewModel
class FinancialViewModel @Inject constructor(
    private val financialRepository: FinancialRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    var financialState by mutableStateOf(FinancialState(isLoading = true))
        private set

    init {
        loadFinancialRecords()
        loadCategories()
    }

    fun loadFinancialRecords() {
        financialState = financialState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {

                financialRepository.getAllRecords().fold(
                    onSuccess = { records ->

                        val income = records.filter { it.isIncome }.sumOf { it.amount }
                        val expense = records.filter { !it.isIncome }.sumOf { it.amount }
                        val balance = income - expense

                        financialState = financialState.copy(
                            records = records.sortedByDescending { it.date },
                            isLoading = false,
                            totalIncome = income,
                            totalExpense = expense,
                            balance = balance,
                            error = null
                        )
                    },
                    onFailure = { exception ->

                        exception.printStackTrace()
                        financialState = financialState.copy(
                            isLoading = false,
                            error = "Firebase bağlantı sorunu: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {

                e.printStackTrace()
                financialState = financialState.copy(
                    isLoading = false,
                    error = "Beklenmeyen hata: ${e.message}"
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val incomeResult = categoryRepository.getIncomeCategories()
                val expenseResult = categoryRepository.getExpenseCategories()

                financialState = financialState.copy(
                    incomeCategories = incomeResult.getOrDefault(emptyList()),
                    expenseCategories = expenseResult.getOrDefault(emptyList())
                )
            } catch (e: Exception) {

            }
        }
    }

    fun addFinancialRecord(
        title: String,
        amount: Double,
        isIncome: Boolean,
        category: String,
        description: String
    ) {
        if (title.isBlank() || amount <= 0) {
            financialState = financialState.copy(error = "Lütfen geçerli bilgiler girin")
            return
        }

        viewModelScope.launch {
            try {
                val record = FinancialRecord(
                    id = "",
                    title = title,
                    amount = amount,
                    isIncome = isIncome,
                    category = category,
                    description = description,
                    date = Date()
                )

                financialRepository.addRecord(record).fold(
                    onSuccess = {
                        println("DEBUG: Record added successfully")
                        loadFinancialRecords()
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to add record: ${exception.message}")
                        financialState = financialState.copy(
                            error = "Kayıt eklenirken hata: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception while adding record: ${e.message}")
                financialState = financialState.copy(
                    error = "Kayıt eklenirken beklenmeyen hata: ${e.message}"
                )
            }
        }
    }

    fun updateFinancialRecord(
        recordId: String,
        title: String,
        amount: Double,
        isIncome: Boolean,
        category: String,
        description: String
    ) {
        if (title.isBlank() || amount <= 0) {
            financialState = financialState.copy(error = "Lütfen geçerli bilgiler girin")
            return
        }

        viewModelScope.launch {
            try {
                val currentRecord = financialState.records.find { it.id == recordId }
                if (currentRecord != null) {
                    val updatedRecord = currentRecord.copy(
                        title = title,
                        amount = amount,
                        isIncome = isIncome,
                        category = category,
                        description = description
                    )

                    financialRepository.updateRecord(updatedRecord).fold(
                        onSuccess = {
                            loadFinancialRecords()
                            clearEditingRecord()
                        },
                        onFailure = { exception ->
                            financialState = financialState.copy(
                                error = "Kayıt güncellenirken hata: ${exception.message}"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                financialState = financialState.copy(
                    error = "Kayıt güncellenirken beklenmeyen hata: ${e.message}"
                )
            }
        }
    }

    fun deleteFinancialRecord(recordId: String) {
        viewModelScope.launch {
            financialRepository.deleteRecord(recordId).fold(
                onSuccess = {
                    loadFinancialRecords()
                },
                onFailure = { exception ->
                    financialState = financialState.copy(
                        error = "Kayıt silinirken hata: ${exception.message}"
                    )
                }
            )
        }
    }

    fun addCustomCategory(categoryName: String, isIncome: Boolean) {
        if (categoryName.isBlank()) {
            financialState = financialState.copy(error = "Kategori adı boş olamaz")
            return
        }

        viewModelScope.launch {
            categoryRepository.addCustomCategory(categoryName, isIncome).fold(
                onSuccess = {
                    loadCategories()
                },
                onFailure = { exception ->
                    financialState = financialState.copy(
                        error = "Kategori eklenirken hata: ${exception.message}"
                    )
                }
            )
        }
    }

    fun startEditingRecord(record: FinancialRecord) {
        financialState = financialState.copy(editingRecord = record)
    }

    fun clearEditingRecord() {
        financialState = financialState.copy(editingRecord = null)
    }

    fun clearError() {
        financialState = financialState.copy(error = null)
    }
}