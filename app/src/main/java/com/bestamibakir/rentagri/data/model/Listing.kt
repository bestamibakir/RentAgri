package com.bestamibakir.rentagri.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bestamibakir.rentagri.data.database.DateConverter
import com.google.firebase.firestore.PropertyName
import java.util.Date

@Entity(tableName = "listings")
@TypeConverters(DateConverter::class)
data class Listing(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val location: String = "",
    val imageUrls: List<String> = emptyList(),
    val machineType: String = "",
    val createdAt: Date = Date(),
    @PropertyName("active")
    val isActive: Boolean = true,

    val lastUpdated: Date = Date()
) 