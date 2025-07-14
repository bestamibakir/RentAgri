package com.bestamibakir.rentagri.data.repository

import android.util.Log
import com.bestamibakir.rentagri.data.model.Offer
import com.bestamibakir.rentagri.data.model.OfferStatus
import com.bestamibakir.rentagri.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfferRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val offersCollection = firestore.collection("offers")
    private val usersCollection = firestore.collection("users")

    suspend fun createOffer(offer: Offer): Result<String> = withContext(Dispatchers.IO) {
        try {
            val document = offersCollection.document()
            val offerWithId = offer.copy(id = document.id)
            document.set(offerWithId).await()
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOffersForListing(listingId: String): Result<List<Offer>> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = offersCollection
                    .whereEqualTo("listingId", listingId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val offers = snapshot.documents.mapNotNull { document ->
                    document.toObject(Offer::class.java)
                }

                Result.success(offers)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getOffersForSeller(sellerId: String): Result<List<Offer>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("OfferRepository", "Querying offers for seller: $sellerId")

                val snapshot = offersCollection
                    .whereEqualTo("sellerId", sellerId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                Log.d("OfferRepository", "Query returned ${snapshot.size()} documents")

                val offers = snapshot.documents.mapNotNull { document ->
                    Log.d("OfferRepository", "Document data: ${document.data}")
                    document.toObject(Offer::class.java)
                }

                Log.d("OfferRepository", "Found ${offers.size} offers for seller $sellerId")
                Result.success(offers)
            } catch (e: Exception) {
                Log.e("OfferRepository", "Error getting offers for seller $sellerId", e)
                Result.failure(e)
            }
        }

    suspend fun getOffersFromBuyer(buyerId: String): Result<List<Offer>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d("OfferRepository", "Querying offers from buyer: $buyerId")

                val snapshot = offersCollection
                    .whereEqualTo("buyerId", buyerId)
                    .get()
                    .await()

                Log.d("OfferRepository", "Buyer offers query returned ${snapshot.size()} documents")

                val offers = snapshot.documents.mapNotNull { document ->
                    Log.d("OfferRepository", "Buyer offer document data: ${document.data}")
                    document.toObject(Offer::class.java)
                }.sortedByDescending { it.createdAt }

                Log.d("OfferRepository", "Found ${offers.size} offers from buyer $buyerId")
                Result.success(offers)
            } catch (e: Exception) {
                Log.e("OfferRepository", "Error getting offers from buyer $buyerId", e)
                Result.failure(e)
            }
        }

    suspend fun updateOfferStatus(
        offerId: String,
        status: OfferStatus
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateData = mapOf(
                "status" to status,
                "respondedAt" to Date()
            )
            offersCollection.document(offerId).update(updateData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOfferStatusWithContactInfo(
        offerId: String,
        status: OfferStatus,
        buyerId: String,
        sellerId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateData = mutableMapOf<String, Any>(
                "status" to status,
                "respondedAt" to Date()
            )

            if (status == OfferStatus.ACCEPTED) {

                val buyerSnapshot = usersCollection.document(buyerId).get().await()
                val buyer = buyerSnapshot.toObject(User::class.java)


                val sellerSnapshot = usersCollection.document(sellerId).get().await()
                val seller = sellerSnapshot.toObject(User::class.java)

                if (buyer != null && seller != null) {
                    updateData["buyerName"] = "${buyer.firstName} ${buyer.lastName}"
                    updateData["buyerPhone"] = buyer.phoneNumber
                    updateData["sellerName"] = "${seller.firstName} ${seller.lastName}"
                    updateData["sellerPhone"] = seller.phoneNumber
                }
            }

            offersCollection.document(offerId).update(updateData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OfferRepository", "Error updating offer status with contact info", e)
            Result.failure(e)
        }
    }

    suspend fun getOfferById(offerId: String): Result<Offer?> = withContext(Dispatchers.IO) {
        try {
            val snapshot = offersCollection.document(offerId).get().await()
            val offer = snapshot.toObject(Offer::class.java)
            Result.success(offer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteOffer(offerId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            offersCollection.document(offerId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun hasUserMadeOffer(buyerId: String, listingId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = offersCollection
                    .whereEqualTo("buyerId", buyerId)
                    .whereEqualTo("listingId", listingId)
                    .limit(1)
                    .get()
                    .await()

                Result.success(!snapshot.isEmpty)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAcceptedOfferForListing(listingId: String, userId: String): Result<Offer?> =
        withContext(Dispatchers.IO) {
            try {
                val buyerSnapshot = offersCollection
                    .whereEqualTo("listingId", listingId)
                    .whereEqualTo("buyerId", userId)
                    .whereEqualTo("status", OfferStatus.ACCEPTED)
                    .limit(1)
                    .get()
                    .await()

                if (!buyerSnapshot.isEmpty) {
                    val offer = buyerSnapshot.documents.first().toObject(Offer::class.java)
                    return@withContext Result.success(offer)
                }

                val sellerSnapshot = offersCollection
                    .whereEqualTo("listingId", listingId)
                    .whereEqualTo("sellerId", userId)
                    .whereEqualTo("status", OfferStatus.ACCEPTED)
                    .limit(1)
                    .get()
                    .await()

                if (!sellerSnapshot.isEmpty) {
                    val offer = sellerSnapshot.documents.first().toObject(Offer::class.java)
                    return@withContext Result.success(offer)
                }

                Result.success(null)
            } catch (e: Exception) {
                Log.e("OfferRepository", "Error getting accepted offer for listing", e)
                Result.failure(e)
            }
        }
} 