package com.bestamibakir.rentagri.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.bestamibakir.rentagri.data.model.MarketItem
import com.bestamibakir.rentagri.data.model.Listing

@Database(
    entities = [
        MarketItem::class,
        Listing::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class RentAgriDatabase : RoomDatabase() {

    abstract fun marketItemDao(): MarketItemDao
    abstract fun listingDao(): ListingDao

    companion object {
        const val DATABASE_NAME = "rentagri_database"
    }
} 