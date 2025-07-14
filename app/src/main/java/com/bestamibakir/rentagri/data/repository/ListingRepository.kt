package com.bestamibakir.rentagri.data.repository

import android.util.Log
import com.bestamibakir.rentagri.data.database.ListingDao
import com.bestamibakir.rentagri.data.model.Listing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ListingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val listingDao: ListingDao
) {
    private val listingsCollection = firestore.collection("listings")
    private val tag = "ListingRepository"

    companion object {
        private const val CACHE_DURATION = 2 * 60 * 60 * 1000L
    }


    fun getAllListingsFlow(): Flow<List<Listing>> = flow {
        try {

            val cachedListings = listingDao.getAllActiveListings()
            emit(cachedListings)


            val freshResult = fetchFromFirebase {
                listingsCollection
                    .whereEqualTo("active", true)
                    .limit(50)
                    .get()
                    .await()
            }

            if (freshResult.isSuccess) {
                val freshListings = freshResult.getOrNull() ?: emptyList()


                updateCache(freshListings)


                emit(freshListings)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in getAllListingsFlow", e)

            val cachedListings = listingDao.getAllActiveListings()
            emit(cachedListings)
        }
    }


    suspend fun getListingById(id: String): Result<Listing?> {
        return try {

            val cachedListing = listingDao.getListingById(id)

            if (cachedListing != null && !isCacheExpired(cachedListing.lastUpdated)) {
                Log.d(tag, "Returning cached listing for ID: $id")
                return Result.success(cachedListing)
            }


            val document = listingsCollection.document(id).get().await()
            val listing = document.toObject(Listing::class.java)?.copy(
                id = document.id,
                lastUpdated = Date()
            )


            listing?.let {
                listingDao.insertListing(it)
            }

            Result.success(listing)
        } catch (e: Exception) {
            Log.e(tag, "Error getting listing by ID: $id", e)


            val cachedListing = listingDao.getListingById(id)
            if (cachedListing != null) {
                Result.success(cachedListing)
            } else {
                Result.failure(e)
            }
        }
    }


    fun getListingsByUserIdFlow(userId: String): Flow<List<Listing>> = flow {
        try {

            val cachedListings = listingDao.getListingsByUserId(userId)
            emit(cachedListings)


            val freshResult = fetchFromFirebase {
                listingsCollection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }

            if (freshResult.isSuccess) {
                val freshListings = freshResult.getOrNull() ?: emptyList()
                updateCache(freshListings)
                emit(freshListings)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in getListingsByUserIdFlow for user: $userId", e)
            val cachedListings = listingDao.getListingsByUserId(userId)
            emit(cachedListings)
        }
    }


    suspend fun getListingsByUserId(userId: String): Result<List<Listing>> {
        return try {
            Log.d(tag, "Querying listings for user: $userId")

            val snapshot = listingsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(tag, "User listings query returned ${snapshot.size()} documents")

            val listings = snapshot.documents.mapNotNull { document ->
                Log.d(tag, "User listing document data: ${document.data}")
                document.toObject(Listing::class.java)?.copy(
                    id = document.id,
                    lastUpdated = Date()
                )
            }


            updateCache(listings)

            Log.d(tag, "Found ${listings.size} listings for user $userId")
            Result.success(listings)
        } catch (e: Exception) {
            Log.e(tag, "Error getting listings for user $userId", e)


            val cachedListings = listingDao.getListingsByUserId(userId)
            Result.success(cachedListings)
        }
    }


    suspend fun getListingsByCity(city: String): Result<List<Listing>> {
        return try {
            Log.d(tag, "Starting getListingsByCity query for city: $city")


            val cachedListings = listingDao.getListingsByCity(city)

            if (cachedListings.isNotEmpty() && !isCacheExpired(cachedListings.firstOrNull()?.lastUpdated)) {
                Log.d(tag, "Returning cached city listings for: $city")
                return Result.success(cachedListings)
            }


            val snapshot = listingsCollection
                .whereEqualTo("active", true)
                .whereEqualTo("location", city)
                .limit(50)
                .get()
                .await()

            Log.d(tag, "City query completed. Document count: ${snapshot.documents.size}")

            val listings = snapshot.documents.mapNotNull { document ->
                Log.d(tag, "Processing city document: ${document.id}")
                try {
                    document.toObject(Listing::class.java)?.copy(
                        id = document.id,
                        lastUpdated = Date()
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Failed to convert city document ${document.id}", e)
                    null
                }
            }.sortedByDescending { it.createdAt }


            updateCache(listings)

            Log.d(tag, "Successfully processed ${listings.size} city listings")
            Result.success(listings)
        } catch (e: Exception) {
            Log.e(tag, "getListingsByCity failed for city: $city", e)

            val cachedListings = listingDao.getListingsByCity(city)
            Result.success(cachedListings)
        }
    }


    suspend fun searchListings(
        city: String? = null,
        machineType: String? = null,
        query: String? = null
    ): Result<List<Listing>> {
        return try {
            var queryBuilder = listingsCollection
                .whereEqualTo("active", true)

            city?.let {
                queryBuilder = queryBuilder.whereEqualTo("location", it)
            }

            machineType?.let {
                queryBuilder = queryBuilder.whereEqualTo("machineType", it)
            }

            val snapshot = queryBuilder
                .limit(50)
                .get()
                .await()

            var listings = snapshot.documents.mapNotNull {
                it.toObject(Listing::class.java)?.copy(
                    id = it.id,
                    lastUpdated = Date()
                )
            }.sortedByDescending { it.createdAt }

            query?.let { searchQuery ->
                val lowerQuery = searchQuery.lowercase()
                listings = listings.filter { listing ->
                    listing.title.lowercase().contains(lowerQuery) ||
                            listing.description.lowercase().contains(lowerQuery)
                }
            }

            updateCache(listings)

            Result.success(listings)
        } catch (e: Exception) {
            Log.e(tag, "searchListings failed", e)

            val localResults = if (query != null) {
                listingDao.searchListingsByText(query)
            } else {
                listingDao.searchListings(city, machineType)
            }

            Result.success(localResults)
        }
    }

    suspend fun createListing(listing: Listing): Result<String> {
        return try {
            val documentRef = listingsCollection.add(listing).await()
            val createdListing = listing.copy(
                id = documentRef.id,
                lastUpdated = Date()
            )

            listingDao.insertListing(createdListing)

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Log.e(tag, "Error creating listing", e)
            Result.failure(e)
        }
    }

    suspend fun updateListing(listing: Listing): Result<Unit> {
        return try {
            val updatedListing = listing.copy(lastUpdated = Date())

            listingsCollection.document(listing.id).set(updatedListing).await()

            listingDao.updateListing(updatedListing)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error updating listing", e)
            Result.failure(e)
        }
    }

    suspend fun deleteListing(id: String): Result<Unit> {
        return try {
            listingsCollection.document(id).delete().await()

            listingDao.deleteListingById(id)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error deleting listing", e)
            Result.failure(e)
        }
    }

    suspend fun deactivateListing(id: String): Result<Unit> {
        return try {
            listingsCollection.document(id)
                .update("active", false)
                .await()

            listingDao.deactivateListing(id)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error deactivating listing", e)
            Result.failure(e)
        }
    }

    suspend fun testFirestoreConnection(): Result<String> {
        return try {
            val testDoc = listingsCollection.limit(1).get().await()
            Result.success("Firestore connection successful. Document count: ${testDoc.size()}")
        } catch (e: Exception) {
            Log.e(tag, "Firestore connection test failed", e)
            Result.failure(e)
        }
    }

    suspend fun clearCache() {
        try {
            listingDao.clearAllListings()
            Log.d(tag, "Listing cache cleared")
        } catch (e: Exception) {
            Log.e(tag, "Error clearing listing cache", e)
        }
    }

    suspend fun clearExpiredCache() {
        try {
            val cutoffTime = System.currentTimeMillis() - CACHE_DURATION
            listingDao.deleteOldListings(cutoffTime)
            Log.d(tag, "Expired listing cache cleared")
        } catch (e: Exception) {
            Log.e(tag, "Error clearing expired listing cache", e)
        }
    }

    suspend fun getCacheStats(): Pair<Int, Long?> {
        return try {
            val count = listingDao.getActiveListingsCount()
            val lastUpdate = listingDao.getLastUpdateTime()
            Pair(count, lastUpdate)
        } catch (e: Exception) {
            Log.e(tag, "Error getting cache stats", e)
            Pair(0, null)
        }
    }

    private suspend fun fetchFromFirebase(query: suspend () -> com.google.firebase.firestore.QuerySnapshot): Result<List<Listing>> {
        return try {
            val snapshot = query()
            val listings = snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Listing::class.java)?.copy(
                        id = document.id,
                        lastUpdated = Date()
                    )
                } catch (e: Exception) {
                    Log.e(tag, "Failed to convert document ${document.id}", e)
                    null
                }
            }
            Result.success(listings)
        } catch (e: Exception) {
            Log.e(tag, "Firebase fetch failed", e)
            Result.failure(e)
        }
    }

    private suspend fun updateCache(listings: List<Listing>) {
        try {
            if (listings.isNotEmpty()) {

                CoroutineScope(Dispatchers.IO).launch {
                    listingDao.insertListings(listings)
                    Log.d(tag, "Cache updated with ${listings.size} listings")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating cache", e)
        }
    }

    private fun isCacheExpired(lastUpdated: Date?): Boolean {
        if (lastUpdated == null) return true
        return System.currentTimeMillis() - lastUpdated.time > CACHE_DURATION
    }
} 