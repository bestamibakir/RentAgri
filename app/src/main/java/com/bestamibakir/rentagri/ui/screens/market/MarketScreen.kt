package com.bestamibakir.rentagri.ui.screens.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.R
import com.bestamibakir.rentagri.data.model.MarketItem
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    viewModel: MarketViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToLogin: (() -> Unit)? = null
) {
    val marketState = viewModel.marketState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = marketState.isLoading
    )

    LaunchedEffect(marketState.error) {
        marketState.error?.let { error ->
            scope.launch {
                if (error.contains("PERMISSION_DENIED") || error.contains("güvenlik kuralları")) {
                    snackbarHostState.showSnackbar(
                        message = "Firebase güvenlik kuralları güncellenmeli. Geliştirici ile iletişime geçin.",
                        duration = androidx.compose.material3.SnackbarDuration.Long
                    )
                } else if (error.contains("UNAUTHENTICATED") || error.contains("giriş yap")) {
                    snackbarHostState.showSnackbar(
                        message = "Bu sayfayı görmek için giriş yapmalısınız.",
                        actionLabel = "Giriş Yap",
                        duration = androidx.compose.material3.SnackbarDuration.Long
                    ).also {
                        if (it == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            onNavigateToLogin?.invoke()
                        }
                    }
                } else {
                    snackbarHostState.showSnackbar(error)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMarketData()
    }

    Scaffold(
        topBar = {
            RentAgriTopAppBar(
                title = "Sebze & Meyve Fiyatları",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.refreshMarketData() },
                modifier = Modifier.fillMaxSize()
            ) {
                ProduceContent(
                    produceItems = marketState.produceItems,
                    displayedItems = marketState.displayedItems,
                    isLoading = marketState.isLoading,
                    searchQuery = marketState.searchQuery,
                    sortOrder = marketState.sortOrder,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onSortClick = { viewModel.toggleSortOrder() }
                )
            }
        }
    }
}

@Composable
fun OfflineIndicator() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.using_offline_data),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MarketTableHeader(
    sortOrder: SortOrder = SortOrder.NONE,
    onSortClick: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ürün Detayları",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.clickable { onSortClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sortLabel = when (sortOrder) {
                    SortOrder.NONE -> "Alfabetik Sıralama"
                    SortOrder.PRICE_ASCENDING -> "Fiyat (Artan)"
                    SortOrder.PRICE_DESCENDING -> "Fiyat (Azalan)"
                }
                Text(
                    text = sortLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.width(4.dp))

                val sortIcon = when (sortOrder) {
                    SortOrder.PRICE_ASCENDING -> Icons.Default.ArrowUpward
                    SortOrder.PRICE_DESCENDING -> Icons.Default.ArrowDownward
                    SortOrder.NONE -> Icons.Default.Sort
                }

                Icon(
                    imageVector = sortIcon,
                    contentDescription = when (sortOrder) {
                        SortOrder.PRICE_ASCENDING -> "Artan fiyat sıralaması"
                        SortOrder.PRICE_DESCENDING -> "Azalan fiyat sıralaması"
                        SortOrder.NONE -> "Sıralama yok"
                    },
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Ürün ara...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Arama"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Temizle"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun MarketItemRow(item: MarketItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))


                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    if (item.productType.isNotEmpty()) {
                        Text(
                            text = "Cins: ${item.productType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }


                    if (item.productVariety.isNotEmpty()) {
                        Text(
                            text = "Tür: ${item.productVariety}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))


                Text(
                    text = "Birim: ${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Text(
                text = formatMoney(item.currentPrice),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
    }
}


fun formatMoney(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(amount)
}


fun formatChangeMoney(change: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return if (change > 0) "+${format.format(change)}" else format.format(change)
}

@Composable
fun ProduceContent(
    produceItems: List<MarketItem>,
    displayedItems: List<MarketItem>,
    isLoading: Boolean,
    searchQuery: String,
    sortOrder: SortOrder,
    onSearchQueryChange: (String) -> Unit,
    onSortClick: () -> Unit
) {
    Column {

        SearchField(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(16.dp)
        )

        MarketTableHeader(
            sortOrder = sortOrder,
            onSortClick = onSortClick
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (displayedItems.isEmpty() && searchQuery.isEmpty()) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz sebze & meyve verisi bulunmuyor",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MarketInfoFooter()
            }
        } else if (displayedItems.isEmpty() && searchQuery.isNotEmpty()) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\"$searchQuery\" için sonuç bulunamadı",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MarketInfoFooter()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayedItems, key = { it.id }) { item ->
                    MarketItemRow(item = item)
                }


                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MarketInfoFooter()
                }
            }
        }
    }
}

@Composable
fun MarketInfoFooter() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Bu bilgiler Hal Kayıt Sistemi'nden alınmıştır.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
} 