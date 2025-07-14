package com.bestamibakir.rentagri.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class FinancialRecord(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val isIncome: Boolean = false,
    val category: String = "",
    val description: String = "",
    @get:PropertyName("date") @set:PropertyName("date")
    var date: Date = Date()
) {
    constructor() : this("", "", "", 0.0, false, "", "", Date())

    fun getTimestamp(): Timestamp = Timestamp(date)
    fun setTimestamp(timestamp: Timestamp) {
        date = timestamp.toDate()
    }
}

enum class RecordType {
    INCOME, EXPENSE
}