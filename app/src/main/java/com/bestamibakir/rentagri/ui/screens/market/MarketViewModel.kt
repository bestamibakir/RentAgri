package com.bestamibakir.rentagri.ui.screens.market

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestamibakir.rentagri.data.model.MarketItem
import com.bestamibakir.rentagri.data.repository.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder {
    NONE,
    PRICE_ASCENDING,
    PRICE_DESCENDING
}

data class MarketState(
    val allItems: List<MarketItem> = emptyList(),
    val displayedItems: List<MarketItem> = emptyList(),
    val produceItems: List<MarketItem> = emptyList(),
    val categories: List<String> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.NONE,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val marketRepository: MarketRepository
) : ViewModel() {

    var marketState by mutableStateOf(MarketState(isLoading = true))
        private set

    init {
        loadMarketData()
    }

    fun loadMarketData() {
        loadAllMarketItems()
    }

    fun refreshMarketData() {
        loadAllMarketItems()
    }

    fun updateSearchQuery(query: String) {
        marketState = marketState.copy(searchQuery = query)
        applyFiltersAndSort()
    }

    fun toggleSortOrder() {
        val newSortOrder = when (marketState.sortOrder) {
            SortOrder.NONE -> SortOrder.PRICE_ASCENDING
            SortOrder.PRICE_ASCENDING -> SortOrder.PRICE_DESCENDING
            SortOrder.PRICE_DESCENDING -> SortOrder.NONE
        }
        marketState = marketState.copy(sortOrder = newSortOrder)
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        var filteredItems = marketState.produceItems


        if (marketState.searchQuery.isNotEmpty()) {
            filteredItems = filteredItems.filter { item ->
                item.name.contains(marketState.searchQuery, ignoreCase = true) ||
                        item.productType.contains(marketState.searchQuery, ignoreCase = true) ||
                        item.productVariety.contains(marketState.searchQuery, ignoreCase = true)
            }
        }


        val sortedItems = when (marketState.sortOrder) {
            SortOrder.PRICE_ASCENDING -> filteredItems.sortedBy { it.currentPrice }
            SortOrder.PRICE_DESCENDING -> filteredItems.sortedByDescending { it.currentPrice }
            SortOrder.NONE -> filteredItems.sortedWith(getTurkishComparator())
        }

        marketState = marketState.copy(displayedItems = sortedItems)
    }

    private fun getTurkishComparator(): Comparator<MarketItem> {
        val turkishCollator = java.text.Collator.getInstance(java.util.Locale("tr", "TR"))
        return compareBy(turkishCollator) { it.name }
    }

    private fun loadAllMarketItems() {
        marketState = marketState.copy(isLoading = true, error = null)

        marketRepository.getAllMarketItems()
            .onEach { items ->

                val produceItems = items.filter { it.category == "Sebze" || it.category == "Meyve" }


                val categories = produceItems.map { it.category }
                    .filter { it.isNotEmpty() }
                    .distinct()
                    .sorted()

                marketState = marketState.copy(
                    allItems = items,
                    produceItems = produceItems,
                    categories = categories,
                    isLoading = false
                )
                applyFiltersAndSort()
            }
            .catch { exception ->
                marketState = marketState.copy(
                    isLoading = false,
                    error = exception.message
                        ?: "Sebze & Meyve verileri yüklenirken bir hata oluştu"
                )
            }
            .launchIn(viewModelScope)
    }


    fun clearCache() {
        viewModelScope.launch {
            try {
                marketRepository.clearOldCache()
            } catch (e: Exception) {

            }
        }
    }


    fun checkOfflineMode(): Boolean {
        return marketState.allItems.isNotEmpty()
    }
}