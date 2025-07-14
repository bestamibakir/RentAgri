package com.bestamibakir.rentagri.data.repository

import android.util.Log
import com.bestamibakir.rentagri.data.database.MarketItemDao
import com.bestamibakir.rentagri.data.model.MarketItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject


class MarketRepository @Inject constructor(
    private val marketItemDao: MarketItemDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val halFiyatlariCollection = firestore.collection("hal_fiyatlari")

    companion object {
        private const val TAG = "MarketRepository"
        private const val CACHE_DURATION_HOURS = 2
    }


    fun getAllMarketItems(): Flow<List<MarketItem>> = flow {
        try {

            val localItems = marketItemDao.getAllMarketItems().first()

            if (localItems.isNotEmpty()) {
                emit(localItems)
            }

            try {
                val remoteItems = fetchFromFirebase()
                if (remoteItems.isNotEmpty()) {

                    marketItemDao.deleteAllMarketItems()
                    marketItemDao.insertMarketItems(remoteItems)
                    emit(remoteItems)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase'den veri çekilemedi, yerel veri kullanılıyor", e)

                if (localItems.isEmpty()) {
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Veri çekme hatası", e)
            throw e
        }
    }.catch { exception ->
        Log.e(TAG, "getAllMarketItems hatası", exception)
        throw exception
    }

    fun getProduceItems(): Flow<List<MarketItem>> = flow {
        try {

            val localItems = marketItemDao.getProduceItems().first()

            if (localItems.isNotEmpty()) {
                emit(localItems)
            }

            try {
                val remoteItems = fetchFromFirebase()
                if (remoteItems.isNotEmpty()) {
                    marketItemDao.deleteAllMarketItems()
                    marketItemDao.insertMarketItems(remoteItems)

                    val produceItems = remoteItems.filter {
                        it.category == "Sebze" || it.category == "Meyve"
                    }
                    emit(produceItems)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase'den veri çekilemedi, yerel veri kullanılıyor", e)
                if (localItems.isEmpty()) {
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sebze-meyve verileri çekme hatası", e)
            throw e
        }
    }.catch { exception ->
        Log.e(TAG, "getProduceItems hatası", exception)
        throw exception
    }

    fun getMarketItemsByCategory(category: String): Flow<List<MarketItem>> = flow {
        try {

            val localItems = marketItemDao.getMarketItemsByCategory(category).first()

            if (localItems.isNotEmpty()) {
                emit(localItems)
            }

            try {
                val remoteItems = fetchFromFirebase()
                if (remoteItems.isNotEmpty()) {
                    marketItemDao.deleteAllMarketItems()
                    marketItemDao.insertMarketItems(remoteItems)

                    val filteredItems = remoteItems.filter {
                        it.category.equals(category, ignoreCase = true)
                    }
                    emit(filteredItems)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase'den veri çekilemedi, yerel veri kullanılıyor", e)
                if (localItems.isEmpty()) {
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Kategori verileri çekme hatası", e)
            throw e
        }
    }.catch { exception ->
        Log.e(TAG, "getMarketItemsByCategory hatası", exception)
        throw exception
    }

    fun getVegetables(): Flow<List<MarketItem>> = marketItemDao.getVegetables()

    fun getFruits(): Flow<List<MarketItem>> = marketItemDao.getFruits()

    suspend fun getMarketItemById(id: String): MarketItem? {
        return try {

            val localItem = marketItemDao.getMarketItemById(id)
            if (localItem != null) {
                return localItem
            }

            val document = halFiyatlariCollection.document(id).get().await()
            val data = document.data

            if (data != null) {
                val item = mapFirebaseDataToMarketItem(document.id, data)
                marketItemDao.insertMarketItem(item)
                item
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ürün detayı çekme hatası", e)
            null
        }
    }

    suspend fun saveMarketItem(item: MarketItem): Result<Unit> {
        return try {
            val itemMap = hashMapOf(
                "urun_adi" to item.name.uppercase(),
                "ortalama_fiyat" to item.currentPrice,
                "birim" to item.unit.uppercase(),
                "created_at" to Timestamp.now(),
                "tarih" to java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale("tr", "TR"))
                    .format(Date())
            )

            if (item.id.isBlank()) {

                val docRef = halFiyatlariCollection.add(itemMap).await()
                val newItem = item.copy(id = docRef.id, lastUpdateDate = Date())
                marketItemDao.insertMarketItem(newItem)
            } else {

                halFiyatlariCollection.document(item.id).set(itemMap).await()
                val updatedItem = item.copy(lastUpdateDate = Date())
                marketItemDao.updateMarketItem(updatedItem)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Ürün kaydetme hatası", e)
            Result.failure(e)
        }
    }

    private suspend fun fetchFromFirebase(): List<MarketItem> {
        val snapshot = halFiyatlariCollection.get().await()

        return snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data ?: return@mapNotNull null
                mapFirebaseDataToMarketItem(doc.id, data)
            } catch (e: Exception) {
                Log.w(TAG, "Firebase veri mapping hatası", e)
                null
            }
        }.sortedWith(getTurkishComparator())
    }

    private fun mapFirebaseDataToMarketItem(docId: String, data: Map<String, Any>): MarketItem {
        val urunAdi = data["urun_adi"] as? String ?: ""
        val urunCinsi = data["urun_cinsi"] as? String ?: ""
        val urunTuru = data["urun_turu"] as? String ?: ""
        val ortalamafiyat = (data["ortalama_fiyat"] as? Number)?.toDouble() ?: 0.0
        val birim = data["birim"] as? String ?: "Kg"
        val timestamp = data["created_at"] as? Timestamp

        return MarketItem(
            id = docId,
            name = formatProductName(urunAdi),
            productType = formatProductType(urunCinsi),
            productVariety = formatProductVariety(urunTuru),
            currentPrice = ortalamafiyat,
            previousPrice = ortalamafiyat,
            unit = birim.lowercase(),
            changePercentage = 0.0,
            lastUpdateDate = timestamp?.toDate() ?: Date(),
            category = determineCategory(urunAdi),
            timestamp = timestamp
        )
    }

    private fun formatProductName(productName: String): String {
        if (productName.isBlank()) return ""

        return productName
            .replace("Geleneksel", "")
            .replace("Konvansiyonel", "")
            .replace("Gelenesel", "")
            .replace("GELENEKSEL", "")
            .replace("KONVANSIYONEL", "")
            .let { cleanedName ->
                formatAllWords(cleanedName)
            }
            .replace("Iyi", "İyi")
            .replace("Tarim", "Tarım")
            .trim()
    }

    private fun formatProductType(productType: String): String {
        if (productType.isBlank()) return ""

        return formatAllWords(productType)
    }

    private fun formatProductVariety(productVariety: String): String {
        if (productVariety.isBlank()) return ""

        return productVariety
            .replace("(Konvansiyonel)", "")
            .replace("(konvansiyonel)", "")
            .replace("(KONVANSIYONEL)", "")
            .replace("Geleneksel", "Klasik")
            .replace("geleneksel", "Klasik")
            .replace("GELENEKSEL", "Klasik")
            .let { cleanedVariety ->
                formatAllWords(cleanedVariety)
            }
            .trim()
            .ifEmpty { "Standart" }
    }

    private fun formatAllWords(text: String): String {
        if (text.isBlank()) return ""

        val result = StringBuilder()
        var i = 0

        while (i < text.length) {
            val char = text[i]

            when {
                char == '(' -> {
                    result.append(char)
                    i++
                    val parenStart = i
                    var parenDepth = 1
                    var parenEnd = i

                    while (parenEnd < text.length && parenDepth > 0) {
                        when (text[parenEnd]) {
                            '(' -> parenDepth++
                            ')' -> parenDepth--
                        }
                        if (parenDepth > 0) parenEnd++
                    }

                    if (parenEnd <= text.length) {
                        val parenContent = text.substring(parenStart, parenEnd)
                        result.append(formatWords(parenContent))
                        i = parenEnd
                    }
                }

                char.isLetterOrDigit() || char == '_' || char == '-' -> {

                    val wordStart = i
                    while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '_' || text[i] == '-')) {
                        i++
                    }
                    val word = text.substring(wordStart, i)
                    result.append(formatWords(word))
                }

                else -> {
                    result.append(char)
                    i++
                }
            }
        }

        return result.toString()
    }

    private fun formatWords(text: String): String {
        return text.lowercase(java.util.Locale("tr", "TR"))
            .split("_", "-", " ")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { char ->
                    if (char.isLowerCase()) {
                        char.titlecase(java.util.Locale("tr", "TR"))
                    } else {
                        char.toString()
                    }
                }
            }
    }

    private fun determineCategory(productName: String): String {
        val name = productName.lowercase(java.util.Locale("tr", "TR"))

        val vegetables = listOf(
            "domates", "salatalik", "salatalık", "patates", "sogan", "soğan",
            "biber", "patlican", "patlıcan", "kabak", "havuc", "havuç",
            "lahana", "karnabahar", "brokoli", "ispanak", "ıspanak",
            "marul", "roka", "maydanoz", "dereotu", "nane", "feslegen", "fesleğen",
            "pirasa", "pırasa", "kereviz", "turp", "pancar", "bamya", "fasulye",
            "bezelye", "nohut", "mercimek", "bulgur", "sarimsak", "sarımsak",
            "zencefil", "acur", "adacayi", "adaçayı", "alabas", "kohlrabi",
            "andiva", "pazi", "pazı", "semizotu"
        )

        val fruits = listOf(
            "elma", "armut", "portakal", "mandalina", "limon", "greyfurt",
            "muz", "uzum", "üzüm", "seftali", "şeftali", "kayisi", "kayısı",
            "erik", "kiraz", "visne", "vişne", "cilek", "çilek", "ahududu",
            "bogurtlen", "böğürtlen", "frambuaz", "karpuz", "kavun",
            "ananas", "kivi", "avokado", "nar", "hurma", "incir", "dut",
            "mango", "papaya", "ejder", "passion"
        )

        val fuels = listOf(
            "benzin", "motorin", "lpg", "diesel", "fuel", "yakit", "yakıt",
            "euro", "kursunuz", "kurşunsuz", "gaz", "mazot"
        )

        return when {
            vegetables.any { name.contains(it) } -> "Sebze"
            fruits.any { name.contains(it) } -> "Meyve"
            fuels.any { name.contains(it) } -> "Akaryakıt"
            else -> "Sebze"
        }
    }

    private fun getTurkishComparator(): Comparator<MarketItem> {
        val turkishCollator = java.text.Collator.getInstance(java.util.Locale("tr", "TR"))
        return compareBy(turkishCollator) { it.name }
    }

    suspend fun clearOldCache() {
        try {
            val cutoffTime = System.currentTimeMillis() - (CACHE_DURATION_HOURS * 60 * 60 * 1000)
            marketItemDao.deleteOldItems(cutoffTime)
        } catch (e: Exception) {
            Log.e(TAG, "Cache temizleme hatası", e)
        }
    }
} 