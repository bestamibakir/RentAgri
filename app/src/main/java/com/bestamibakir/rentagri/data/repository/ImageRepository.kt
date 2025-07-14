package com.bestamibakir.rentagri.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.util.UUID

class ImageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    private val storageRef = storage.reference
    private val useMockImages = false

    suspend fun uploadListingImage(imageUri: Uri, listingId: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Kullanıcı oturum açmamış")
            )

            if (useMockImages) {
                delay(1000)
                val mockImageUrl = generateMockImageUrl()
                Log.d("ImageRepository", "Mock image upload successful: $mockImageUrl")
                return Result.success(mockImageUrl)
            }


            val fileName = "listing_images/${userId}/${listingId}/${UUID.randomUUID()}.jpg"
            Log.d("ImageRepositoryPath", "Attempting to upload to path: $fileName")
            val imageRef = storageRef.child(fileName)


            val uploadTask = imageRef.putFile(imageUri).await()


            val downloadUrl = imageRef.downloadUrl.await().toString()

            Log.d("ImageRepository", "Upload successful")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Image upload failed", e)

            if (useMockImages) {

                delay(500)
                val mockImageUrl = generateMockImageUrl()
                Log.d("ImageRepository", "Fallback to mock image: $mockImageUrl")
                return Result.success(mockImageUrl)
            }

            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(imageUri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Kullanıcı oturum açmamış")
            )

            if (useMockImages) {
                delay(1000)
                val mockImageUrl = generateMockImageUrl()
                return Result.success(mockImageUrl)
            }


            val fileName = "profile_images/${userId}/profile_${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Profile image upload failed", e)
            Result.failure(e)
        }
    }


    suspend fun uploadProductImage(imageUri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Kullanıcı oturum açmamış")
            )

            if (useMockImages) {
                delay(1000)
                val mockImageUrl = generateMockImageUrl()
                return Result.success(mockImageUrl)
            }


            val fileName = "product_images/${userId}/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Product image upload failed", e)
            Result.failure(e)
        }
    }


    suspend fun uploadFinancialDocument(documentUri: Uri, documentName: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(
                Exception("Kullanıcı oturum açmamış")
            )

            if (useMockImages) {
                delay(1000)
                return Result.success("mock_document_url")
            }


            val fileName = "financial_documents/${userId}/${documentName}_${UUID.randomUUID()}"
            val documentRef = storageRef.child(fileName)

            val uploadTask = documentRef.putFile(documentUri).await()
            val downloadUrl = documentRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Financial document upload failed", e)
            Result.failure(e)
        }
    }


    @Deprecated("Use uploadListingImage instead")
    suspend fun uploadImage(imageUri: Uri): Result<String> {

        val tempListingId = UUID.randomUUID().toString()
        return uploadListingImage(imageUri, tempListingId)
    }


    suspend fun uploadListingImages(imageUris: List<Uri>, listingId: String): Result<List<String>> {
        return try {
            val uploadedUrls = mutableListOf<String>()

            for (uri in imageUris) {
                val result = uploadListingImage(uri, listingId)
                result.fold(
                    onSuccess = { url -> uploadedUrls.add(url) },
                    onFailure = { exception ->
                        Log.e("ImageRepository", "Failed to upload image: $uri", exception)

                        if (useMockImages) {
                            val mockUrl = generateMockImageUrl()
                            uploadedUrls.add(mockUrl)
                        } else {
                            return Result.failure(exception)
                        }
                    }
                )
            }

            Result.success(uploadedUrls)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Multiple image upload failed", e)
            Result.failure(e)
        }
    }


    @Deprecated("Use uploadListingImages instead")
    suspend fun uploadImages(imageUris: List<Uri>): Result<List<String>> {
        val tempListingId = UUID.randomUUID().toString()
        return uploadListingImages(imageUris, tempListingId)
    }

    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            if (useMockImages) {
                Log.d("ImageRepository", "Mock image delete: $imageUrl")
                return Result.success(Unit)
            }

            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Image delete failed", e)
            Result.failure(e)
        }
    }

    suspend fun deleteImages(imageUrls: List<String>): Result<Unit> {
        return try {
            for (imageUrl in imageUrls) {
                deleteImage(imageUrl)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ImageRepository", "Multiple image delete failed", e)
            Result.failure(e)
        }
    }

    private fun generateMockImageUrl(): String {
        val tractorImages = listOf(
            "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1581833971358-2c8b550f87b3?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1595439458408-63a5a36d5f1a?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1576662712957-9c79ae1280f8?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1625246333195-78d9c38ad449?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1554310603-d39d43033735?w=500&h=400&fit=crop",
            "https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=500&h=400&fit=crop"
        )

        return tractorImages.random()
    }
} 