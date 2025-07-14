package com.bestamibakir.rentagri.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bestamibakir.rentagri.data.database.DateConverter
import com.google.firebase.Timestamp
import java.util.Date

@Keep
@Entity(tableName = "market_items")
@TypeConverters(DateConverter::class)
data class MarketItem(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val productType: String = "",
    val productVariety: String = "",
    val currentPrice: Double = 0.0,
    val previousPrice: Double = 0.0,
    val unit: String = "KG",
    val changePercentage: Double = 0.0,
    val lastUpdateDate: Date = Date(),
    val category: String = "",
    val timestamp: Timestamp? = null
) {
    fun getUpdateDate(): Date {
        return timestamp?.toDate() ?: lastUpdateDate
    }
} 