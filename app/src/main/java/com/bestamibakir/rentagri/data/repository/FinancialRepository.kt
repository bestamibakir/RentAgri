package com.bestamibakir.rentagri.data.repository

import android.util.Log
import com.bestamibakir.rentagri.data.model.FinancialRecord
import com.bestamibakir.rentagri.data.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FinancialRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository
) {
    private val financialCollection = firestore.collection("financial_records")
    private val tag = "FinancialRepository"

    suspend fun getAllRecords(): Result<List<FinancialRecord>> {
        return try {
            Log.d(tag, "Starting getAllRecords()")

            val currentUser = userRepository.getCurrentUser()
            if (currentUser == null) {
                Log.w(tag, "No current user found")
                return Result.failure(Exception("Kullanıcı girişi yapılmamış. Lütfen giriş yapın."))
            }

            Log.d(tag, "Current user ID: ${currentUser.id}")

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser == null) {
                Log.w(tag, "Firebase user is null")
                return Result.failure(Exception("Firebase kimlik doğrulaması gerekli. Lütfen çıkış yapıp tekrar giriş yapın."))
            }

            Log.d(tag, "Firebase user UID: ${firebaseUser.uid}")
            Log.d(tag, "Querying financial_records collection...")

            val snapshot = financialCollection
                .whereEqualTo("userId", currentUser.id)
                .get()
                .await()

            Log.d(tag, "Query completed successfully. Document count: ${snapshot.documents.size}")

            val records = mutableListOf<FinancialRecord>()

            for (doc in snapshot.documents) {
                try {
                    Log.d(tag, "Processing document ${doc.id}")

                    val data = doc.data
                    if (data != null) {
                        val record = FinancialRecord(
                            id = doc.id,
                            userId = data["userId"] as? String ?: "",
                            title = data["title"] as? String ?: "",
                            amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
                            isIncome = data["isIncome"] as? Boolean ?: false,
                            category = data["category"] as? String ?: "",
                            description = data["description"] as? String ?: "",
                            date = (data["date"] as? Timestamp)?.toDate() ?: java.util.Date()
                        )

                        Log.d(tag, "Successfully created record: ${record.title}")
                        records.add(record)
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error processing document ${doc.id}", e)
                }
            }


            val sortedRecords = records.sortedByDescending { it.date }

            Log.d(tag, "Successfully loaded and sorted ${sortedRecords.size} records")
            Result.success(sortedRecords)

        } catch (e: Exception) {
            Log.e(tag, "Exception in getAllRecords", e)

            val errorMessage = when {
                e.message?.contains("PERMISSION_DENIED") == true ->
                    "Firestore erişim izni reddedildi. Firestore güvenlik kurallarını kontrol edin."

                e.message?.contains("UNAVAILABLE") == true ->
                    "Firebase servisi şu anda kullanılamıyor. İnternet bağlantınızı kontrol edin."

                e.message?.contains("DEADLINE_EXCEEDED") == true ->
                    "Firebase bağlantı zaman aşımı. Lütfen tekrar deneyin."

                e.message?.contains("UNAUTHENTICATED") == true ->
                    "Firebase kimlik doğrulaması başarısız. Lütfen çıkış yapıp tekrar giriş yapın."

                else -> "Kayıtlar yüklenirken hata oluştu: ${e.message}"
            }

            Result.failure(Exception(errorMessage))
        }
    }


    suspend fun getAllFinancialRecords(): Result<List<FinancialRecord>> = getAllRecords()

    suspend fun getRecordById(id: String): Result<FinancialRecord?> {
        return try {
            val document = financialCollection.document(id).get().await()
            val record = document.toObject(FinancialRecord::class.java)?.copy(id = document.id)

            Result.success(record)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRecord(record: FinancialRecord): Result<String> {
        return try {
            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(Exception("Kullanıcı girişi yapılmamış"))

            Log.d(tag, "Adding record for user: ${currentUser.id}")


            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser == null) {
                Log.w(tag, "Firebase user is null during add operation")
                return Result.failure(Exception("Firebase kimlik doğrulaması gerekli. Lütfen çıkış yapıp tekrar giriş yapın."))
            }

            val recordMap = hashMapOf(
                "userId" to currentUser.id,
                "title" to record.title,
                "amount" to record.amount,
                "isIncome" to record.isIncome,
                "category" to record.category,
                "description" to record.description,
                "date" to Timestamp(record.date)
            )

            Log.d(tag, "Record data to save: ${record.title}, amount: ${record.amount}")

            val documentRef = financialCollection.add(recordMap).await()
            Log.d(tag, "Record saved successfully with ID: ${documentRef.id}")

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Log.e(tag, "Error adding record", e)

            val errorMessage = when {
                e.message?.contains("PERMISSION_DENIED") == true ->
                    "Kayıt ekleme izni reddedildi. Firestore güvenlik kurallarını kontrol edin."

                e.message?.contains("UNAVAILABLE") == true ->
                    "Firebase servisi şu anda kullanılamıyor. İnternet bağlantınızı kontrol edin."

                e.message?.contains("UNAUTHENTICATED") == true ->
                    "Firebase kimlik doğrulaması başarısız. Lütfen çıkış yapıp tekrar giriş yapın."

                else -> "Kayıt eklenirken hata: ${e.message}"
            }

            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun updateRecord(record: FinancialRecord): Result<Unit> {
        return try {
            financialCollection.document(record.id).set(record).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecord(id: String): Result<Unit> {
        return try {
            financialCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}