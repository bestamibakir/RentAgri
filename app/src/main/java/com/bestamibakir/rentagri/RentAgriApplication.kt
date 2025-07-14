package com.bestamibakir.rentagri

import android.app.Application
import android.util.Log
import com.bestamibakir.rentagri.BuildConfig
import com.bestamibakir.rentagri.data.repository.MarketRepository
import com.bestamibakir.rentagri.data.repository.ListingRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RentAgriApplication : Application() {

    @Inject
    lateinit var marketRepository: MarketRepository

    @Inject
    lateinit var listingRepository: ListingRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        Log.d("RentAgriApp", "Initializing Firebase...")

        try {

            FirebaseApp.initializeApp(this)


            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            firestore.firestoreSettings = settings


            val auth = FirebaseAuth.getInstance()


            if (BuildConfig.DEBUG) {
                Log.d("RentAgriApp", "Debug mode - Firebase Auth settings optimized for emulator")


                auth.addAuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        Log.d("RentAgriApp", "User signed in: ${user.email}")
                    } else {
                        Log.d("RentAgriApp", "User signed out")
                    }
                }
            }

            Log.d("RentAgriApp", "Firebase initialized successfully")
            Log.d("RentAgriApp", "FirebaseApp name: ${FirebaseApp.getInstance().name}")
            Log.d(
                "RentAgriApp",
                "Firebase project ID: ${FirebaseApp.getInstance().options.projectId}"
            )

        } catch (e: Exception) {
            Log.e("RentAgriApp", "Firebase initialization failed", e)
        }


        clearOldCache()

        Log.d("RentAgriApp", "Application started successfully")
    }


    private fun clearOldCache() {
        applicationScope.launch {
            try {

                if (::marketRepository.isInitialized) {
                    marketRepository.clearOldCache()
                    Log.d("RentAgriApp", "Old market cache cleared successfully")
                }


                if (::listingRepository.isInitialized) {
                    listingRepository.clearExpiredCache()
                    Log.d("RentAgriApp", "Old listing cache cleared successfully")
                }

            } catch (e: Exception) {
                Log.w("RentAgriApp", "Cache clearing failed", e)
            }
        }
    }
} 