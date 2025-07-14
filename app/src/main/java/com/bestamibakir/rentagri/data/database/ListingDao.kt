package com.bestamibakir.rentagri.data.database

import androidx.room.*
import com.bestamibakir.rentagri.data.model.Listing
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {

    @Query("SELECT * FROM listings WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveListingsFlow(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE isActive = 1 ORDER BY createdAt DESC LIMIT 50")
    suspend fun getAllActiveListings(): List<Listing>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: String): Listing?

    @Query("SELECT * FROM listings WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getListingsByUserId(userId: String): List<Listing>

    @Query("SELECT * FROM listings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getListingsByUserIdFlow(userId: String): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE location = :city AND isActive = 1 ORDER BY createdAt DESC")
    suspend fun getListingsByCity(city: String): List<Listing>

    @Query(
        """
        SELECT * FROM listings 
        WHERE isActive = 1 
        AND (:city IS NULL OR location = :city)
        AND (:machineType IS NULL OR machineType = :machineType)
        ORDER BY createdAt DESC
        LIMIT 50
    """
    )
    suspend fun searchListings(
        city: String? = null,
        machineType: String? = null
    ): List<Listing>

    @Query(
        """
        SELECT * FROM listings 
        WHERE isActive = 1 
        AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
        LIMIT 50
    """
    )
    suspend fun searchListingsByText(query: String): List<Listing>

    @Query("SELECT COUNT(*) FROM listings WHERE isActive = 1")
    suspend fun getActiveListingsCount(): Int

    @Query("SELECT COUNT(*) FROM listings WHERE userId = :userId")
    suspend fun getUserListingsCount(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: Listing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<Listing>)

    @Update
    suspend fun updateListing(listing: Listing)

    @Delete
    suspend fun deleteListing(listing: Listing)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListingById(id: String)

    @Query("UPDATE listings SET isActive = 0 WHERE id = :id")
    suspend fun deactivateListing(id: String)

    @Query("DELETE FROM listings WHERE lastUpdated < :cutoffTime")
    suspend fun deleteOldListings(cutoffTime: Long)

    @Query("DELETE FROM listings")
    suspend fun clearAllListings()

    @Query("SELECT MAX(lastUpdated) FROM listings")
    suspend fun getLastUpdateTime(): Long?
} 