package com.bestamibakir.rentagri.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository
) {
    private val categoriesCollection = firestore.collection("user_categories")
    private val tag = "CategoryRepository"

    private val defaultIncomeCategories = listOf(
        "Ürün Satışı", "Kira Geliri", "Devlet Desteği", "Diğer Gelir"
    )

    private val defaultExpenseCategories = listOf(
        "Tohum", "Gübre", "Yakıt", "Ekipman", "İşçilik", "Kira Gideri", "Diğer Gider"
    )

    suspend fun getIncomeCategories(): Result<List<String>> {
        return try {
            val currentUser = userRepository.getCurrentUser()
            if (currentUser == null) {
                return Result.success(defaultIncomeCategories)
            }

            val customCategories = getUserCustomCategories(currentUser.id, true)
            val allCategories = (defaultIncomeCategories + customCategories).distinct()

            Log.d(tag, "Loaded ${allCategories.size} income categories")
            Result.success(allCategories)
        } catch (e: Exception) {
            Log.e(tag, "Error getting income categories", e)
            Result.success(defaultIncomeCategories)
        }
    }

    suspend fun getExpenseCategories(): Result<List<String>> {
        return try {
            val currentUser = userRepository.getCurrentUser()
            if (currentUser == null) {
                return Result.success(defaultExpenseCategories)
            }

            val customCategories = getUserCustomCategories(currentUser.id, false)
            val allCategories = (defaultExpenseCategories + customCategories).distinct()

            Log.d(tag, "Loaded ${allCategories.size} expense categories")
            Result.success(allCategories)
        } catch (e: Exception) {
            Log.e(tag, "Error getting expense categories", e)
            Result.success(defaultExpenseCategories)
        }
    }

    suspend fun addCustomCategory(categoryName: String, isIncome: Boolean): Result<Unit> {
        return try {
            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(Exception("Kullanıcı girişi yapılmamış"))

            val firebaseUser = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(Exception("Firebase kimlik doğrulaması gerekli"))

            val categoryData = hashMapOf(
                "userId" to currentUser.id,
                "categoryName" to categoryName.trim(),
                "isIncome" to isIncome,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            categoriesCollection.add(categoryData).await()
            Log.d(tag, "Custom category added: $categoryName")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error adding custom category", e)
            Result.failure(Exception("Kategori eklenirken hata: ${e.message}"))
        }
    }

    private suspend fun getUserCustomCategories(userId: String, isIncome: Boolean): List<String> {
        return try {
            val snapshot = categoriesCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isIncome", isIncome)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.getString("categoryName")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting user custom categories", e)
            emptyList()
        }
    }
} 