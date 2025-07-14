package com.bestamibakir.rentagri.data.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Offer(
    val id: String = "",
    val listingId: String = "",
    val buyerId: String = "",
    val sellerId: String = "",
    val amount: Double = 0.0,
    val message: String = "",
    @PropertyName("status")
    val status: OfferStatus = OfferStatus.PENDING,
    val createdAt: Date = Date(),
    val respondedAt: Date? = null,

    val buyerName: String = "",
    val buyerPhone: String = "",
    val sellerName: String = "",
    val sellerPhone: String = ""
)

enum class OfferStatus {
    PENDING,
    ACCEPTED,
    REJECTED
} 