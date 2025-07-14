package com.bestamibakir.rentagri.ui.screens.listings

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import com.bestamibakir.rentagri.utils.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateListingScreen(
    viewModel: CreateListingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val createListingState = viewModel.createListingState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedMachineType by remember { mutableStateOf("") }
    var showMachineTypeDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }


    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)


    val hasRequiredPermissions = remember(permissionsState.permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsState.permissions.find { it.permission == Manifest.permission.READ_MEDIA_IMAGES }?.status?.isGranted == true
        } else {
            permissionsState.permissions.find { it.permission == Manifest.permission.READ_EXTERNAL_STORAGE }?.status?.isGranted == true
        }
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.uploadImages(uris)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {

        }
    }


    LaunchedEffect(createListingState.isSuccess) {
        if (createListingState.isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("İlan başarıyla oluşturuldu!")
            }
            onNavigateBack()
        }
    }


    LaunchedEffect(createListingState.error) {
        createListingState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            RentAgriTopAppBar(
                title = "İlan Oluştur",
                onBackClick = {
                    if (isFormChanged(
                            title,
                            description,
                            priceText,
                            location,
                            selectedMachineType,
                            createListingState.uploadedImageUrls
                        )
                    ) {
                        showConfirmDialog = true
                    } else {
                        onNavigateBack()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        if (createListingState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("İlan oluşturuluyor...")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("İlan Başlığı *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Title,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMachineTypeDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Makine Tipi *",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = selectedMachineType.ifBlank { "Makine tipini seçin" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedMachineType.isBlank())
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }


                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    minLines = 3,
                    maxLines = 6
                )


                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Günlük Kiralama Fiyatı (TL) *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCityDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Şehir/İl *",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = location.ifBlank { "Şehir seçin" },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (location.isBlank())
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                HorizontalDivider()


                PhotoUploadSection(
                    uploadedImages = createListingState.uploadedImageUrls,
                    isUploading = createListingState.isUploadingImages,
                    onRemoveImage = viewModel::removeUploadedImage,
                    hasPermissions = hasRequiredPermissions,
                    onAddPhotosClick = {
                        if (hasRequiredPermissions) {
                            galleryLauncher.launch("image/*")
                        } else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }
                )


                if (!hasRequiredPermissions) {
                    PermissionInfoCard(
                        permissionsState = permissionsState,
                        currentAndroidVersion = Build.VERSION.SDK_INT,
                        onRequestPermissions = {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        viewModel.createListing(
                            title = title.trim(),
                            description = description.trim(),
                            price = price,
                            location = location.trim(),
                            machineType = selectedMachineType
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !createListingState.isLoading &&
                            !createListingState.isUploadingImages &&
                            title.isNotBlank() &&
                            description.isNotBlank() &&
                            location.isNotBlank() &&
                            selectedMachineType.isNotBlank() &&
                            priceText.toDoubleOrNull() != null &&
                            priceText.toDoubleOrNull()!! > 0
                ) {
                    if (createListingState.isUploadingImages) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fotoğraflar yükleniyor...")
                    } else {
                        Text("İlan Oluştur")
                    }
                }
            }
        }
    }


    if (showMachineTypeDialog) {
        MachineTypeSelectionDialog(
            selectedType = selectedMachineType,
            onTypeSelected = { type ->
                selectedMachineType = type
                showMachineTypeDialog = false
            },
            onDismiss = { showMachineTypeDialog = false }
        )
    }


    if (showCityDialog) {
        CitySelectionDialog(
            selectedCity = location,
            onCitySelected = { city ->
                location = city
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false }
        )
    }


    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Sayfadan Ayrılma") },
            text = { Text("Değişiklikleriniz kaydedilmeyecek. Devam etmek istiyor musunuz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun PhotoUploadSection(
    uploadedImages: List<String>,
    isUploading: Boolean,
    onRemoveImage: (Int) -> Unit,
    hasPermissions: Boolean,
    onAddPhotosClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Makine Fotoğrafları",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${uploadedImages.size}/5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Makinenizin fotoğraflarını ekleyerek daha fazla ilgi çekin",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))


            if (hasPermissions) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "İzinler verildi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable(enabled = !isUploading && uploadedImages.size < 5) {
                        onAddPhotosClick()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (hasPermissions)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (hasPermissions)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Yükleniyor...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (hasPermissions) Icons.Default.Add else Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = if (hasPermissions)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (hasPermissions) {
                                    if (uploadedImages.isEmpty()) "Fotoğraf Ekle" else "Daha Fazla Ekle"
                                } else {
                                    "İzin Ver ve Fotoğraf Ekle"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (hasPermissions)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }


            if (uploadedImages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uploadedImages) { index, imageUrl ->
                        ImageItem(
                            imageUrl = imageUrl,
                            onRemove = { onRemoveImage(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageItem(
    imageUrl: String,
    onRemove: () -> Unit
) {
    Box {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
        )


        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-8).dp)
                .size(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Kaldır",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionInfoCard(
    permissionsState: com.google.accompanist.permissions.MultiplePermissionsState,
    currentAndroidVersion: Int,
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fotoğraf İzni Gerekli",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (currentAndroidVersion >= Build.VERSION_CODES.TIRAMISU) {
                    "Android 13+ için medya dosyalarına erişim izni gereklidir. Bu izin fotoğraf seçmenizi sağlar."
                } else {
                    "Fotoğraf seçmek için depolama alanına erişim izni gereklidir."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))


            Column {
                permissionsState.permissions.forEach { permission ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (permission.status.isGranted)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (permission.status.isGranted)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (permission.permission) {
                                Manifest.permission.CAMERA -> "Kamera"
                                Manifest.permission.READ_EXTERNAL_STORAGE -> "Depolama (Android 12-)"
                                Manifest.permission.READ_MEDIA_IMAGES -> "Medya (Android 13+)"
                                else -> permission.permission
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (permission.status.isGranted)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRequestPermissions,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("İzin Ver")
                }

                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tekrar Dene")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Not: Eğer izinler verildi ama hala bu mesajı görüyorsanız, uygulamayı yeniden başlatmayı deneyin.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MachineTypeSelectionDialog(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Makine Tipi Seçin",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn {
                items(Constants.MACHINE_TYPES) { machineType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTypeSelected(machineType) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == machineType,
                            onClick = { onTypeSelected(machineType) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = machineType,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}

@Composable
fun CitySelectionDialog(
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Şehir Seçin",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn {
                items(Constants.TURKISH_CITIES) { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCitySelected(city) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}


private fun isFormChanged(
    title: String,
    description: String,
    priceText: String,
    location: String,
    machineType: String,
    imageUrls: List<String>
): Boolean {
    return title.isNotBlank() || description.isNotBlank() || priceText.isNotBlank() ||
            location.isNotBlank() || machineType.isNotBlank() || imageUrls.isNotEmpty()
} 