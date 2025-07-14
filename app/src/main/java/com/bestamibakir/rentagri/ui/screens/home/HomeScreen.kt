package com.bestamibakir.rentagri.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.R
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import com.bestamibakir.rentagri.ui.theme.EarthGreen40
import com.bestamibakir.rentagri.ui.theme.EarthGreen60
import com.bestamibakir.rentagri.ui.theme.EarthGreen80
import com.bestamibakir.rentagri.ui.theme.GoldenYellow60
import com.bestamibakir.rentagri.ui.theme.GoldenYellow80
import com.bestamibakir.rentagri.ui.theme.RentAgriTheme
import com.bestamibakir.rentagri.ui.theme.SoftCream
import com.bestamibakir.rentagri.ui.theme.WarmBrown60
import com.bestamibakir.rentagri.ui.theme.WarmBrown80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToListings: () -> Unit,
    onNavigateToFinancial: () -> Unit,
    onNavigateToMarket: () -> Unit
) {
    val uiState = viewModel.homeState

    LaunchedEffect(key1 = true) {
        viewModel.loadUserData()
        viewModel.loadWeatherData()
    }

    Scaffold(
        topBar = {
            RentAgriTopAppBar(title = "RentAgri")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftCream)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                WelcomeSection(
                    userName = uiState.user?.firstName ?: "Kullanıcı",
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                WeatherCard(
                    cityName = uiState.user?.city ?: "",
                    temperature = uiState.weatherTemperature,
                    weatherDescription = uiState.weatherDescription,
                    isLoading = uiState.isWeatherLoading,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Hizmetlerimiz",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = EarthGreen40,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CleanMenuButton(
                            icon = Icons.Default.Agriculture,
                            title = "Kiralık Makineler",
                            subtitle = "Tarım makineleri",
                            backgroundColor = EarthGreen80,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToListings
                        )

                        CleanMenuButton(
                            icon = Icons.Default.AttachMoney,
                            title = "Gelir-Gider Takibi",
                            subtitle = "Mali yönetim",
                            backgroundColor = GoldenYellow80,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToFinancial
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CleanMenuButton(
                            icon = Icons.Default.ShowChart,
                            title = "Borsa",
                            subtitle = "Piyasa fiyatları",
                            backgroundColor = WarmBrown80,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMarket
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun WelcomeSection(
    userName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Hoş geldin,",
                style = MaterialTheme.typography.titleMedium,
                color = EarthGreen40.copy(alpha = 0.7f)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = EarthGreen40
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bugün hangi işlemlerle yardımcı olabilirim?",
                style = MaterialTheme.typography.bodyMedium,
                color = EarthGreen40.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun WeatherCard(
    cityName: String,
    temperature: Double?,
    weatherDescription: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EarthGreen60)
                }
            } else if (temperature != null && weatherDescription != null) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Hava Durumu",
                                tint = GoldenYellow60,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = cityName,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = EarthGreen40
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = weatherDescription,
                            style = MaterialTheme.typography.bodyLarge,
                            color = EarthGreen40.copy(alpha = 0.7f)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = EarthGreen80.copy(alpha = 0.1f),
                        modifier = Modifier.size(70.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${temperature.toInt()}°C",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = EarthGreen40,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hava durumu bilgisi yüklenemedi",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = EarthGreen40.copy(alpha = 0.7f),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CleanMenuButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Surface(
                shape = CircleShape,
                color = backgroundColor.copy(alpha = 0.15f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp),
                        tint = backgroundColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                color = EarthGreen40,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = EarthGreen40.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RentAgriTheme {
        HomeScreen(
            onNavigateToListings = {},
            onNavigateToFinancial = {},
            onNavigateToMarket = {}
        )
    }
} 