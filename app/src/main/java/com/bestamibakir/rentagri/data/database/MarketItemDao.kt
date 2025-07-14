package com.bestamibakir.rentagri.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bestamibakir.rentagri.data.model.MarketItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketItemDao {

    @Query("SELECT * FROM market_items ORDER BY name ASC")
    fun getAllMarketItems(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE category = :category ORDER BY name ASC")
    fun getMarketItemsByCategory(category: String): Flow<List<MarketItem>>


    @Query("SELECT * FROM market_items WHERE category = 'Sebze' ORDER BY name ASC")
    fun getVegetables(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE category = 'Meyve' ORDER BY name ASC")
    fun getFruits(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE category IN ('Sebze', 'Meyve') ORDER BY name ASC")
    fun getProduceItems(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE category = 'Akaryakıt' ORDER BY name ASC")
    fun getFuels(): Flow<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE id = :id")
    suspend fun getMarketItemById(id: String): MarketItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketItem(item: MarketItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketItems(items: List<MarketItem>)

    @Update
    suspend fun updateMarketItem(item: MarketItem)

    @Delete
    suspend fun deleteMarketItem(item: MarketItem)

    @Query("DELETE FROM market_items")
    suspend fun deleteAllMarketItems()

    @Query("DELETE FROM market_items WHERE lastUpdateDate < :cutoffDate")
    suspend fun deleteOldItems(cutoffDate: Long)

    @Query("SELECT COUNT(*) FROM market_items")
    suspend fun getItemCount(): Int
} 