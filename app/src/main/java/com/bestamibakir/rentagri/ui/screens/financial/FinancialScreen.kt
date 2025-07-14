package com.bestamibakir.rentagri.ui.screens.financial

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bestamibakir.rentagri.data.model.FinancialRecord
import com.bestamibakir.rentagri.ui.components.RentAgriTopAppBar
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FinancialScreen(
    viewModel: FinancialViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReports: (() -> Unit)? = null
) {
    val financialState = viewModel.financialState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tabIndex by remember { mutableIntStateOf(0) }


    LaunchedEffect(financialState.error) {
        financialState.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }


    LaunchedEffect(financialState.editingRecord) {
        showEditDialog = financialState.editingRecord != null
    }


    val filteredRecords = when (tabIndex) {
        0 -> financialState.records
        1 -> financialState.records.filter { it.isIncome }
        2 -> financialState.records.filter { !it.isIncome }
        else -> financialState.records
    }

    Scaffold(
        topBar = {
            RentAgriTopAppBar(
                title = "Gelir-Gider Takibi",
                onBackClick = onNavigateBack,
                actions = {
                    if (onNavigateToReports != null) {
                        IconButton(onClick = onNavigateToReports) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Raporlar",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Kayıt Ekle"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            FinancialSummaryCards(
                income = financialState.totalIncome,
                expense = financialState.totalExpense,
                balance = financialState.balance
            )

            TabRow(selectedTabIndex = tabIndex) {
                listOf("Tümü", "Gelirler", "Giderler").forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (financialState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (filteredRecords.isEmpty()) {
                    Text(
                        text = "Kayıt bulunamadı",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredRecords, key = { it.id }) { record ->
                            FinancialRecordItem(
                                record = record,
                                onItemClick = { viewModel.startEditingRecord(record) },
                                onDeleteClicked = { viewModel.deleteFinancialRecord(record.id) }
                            )
                        }
                    }
                }
            }
        }


        if (showAddDialog) {
            FinancialRecordDialog(
                title = "Kayıt Ekle",
                incomeCategories = financialState.incomeCategories,
                expenseCategories = financialState.expenseCategories,
                onDismiss = { showAddDialog = false },
                onRecordSaved = { title, amount, isIncome, category, description ->
                    viewModel.addFinancialRecord(title, amount, isIncome, category, description)
                    showAddDialog = false
                },
                onAddCategory = { categoryName, isIncome ->
                    viewModel.addCustomCategory(categoryName, isIncome)
                }
            )
        }


        if (showEditDialog && financialState.editingRecord != null) {
            FinancialRecordDialog(
                title = "Kayıt Düzenle",
                incomeCategories = financialState.incomeCategories,
                expenseCategories = financialState.expenseCategories,
                editingRecord = financialState.editingRecord,
                onDismiss = {
                    showEditDialog = false
                    viewModel.clearEditingRecord()
                },
                onRecordSaved = { title, amount, isIncome, category, description ->
                    viewModel.updateFinancialRecord(
                        financialState.editingRecord!!.id,
                        title, amount, isIncome, category, description
                    )
                    showEditDialog = false
                },
                onAddCategory = { categoryName, isIncome ->
                    viewModel.addCustomCategory(categoryName, isIncome)
                }
            )
        }
    }
}

@Composable
fun FinancialSummaryCards(
    income: Double,
    expense: Double,
    balance: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        SummaryCard(
            title = "Gelir",
            amount = income,
            icon = Icons.Default.ArrowUpward,
            iconTint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))


        SummaryCard(
            title = "Gider",
            amount = expense,
            icon = Icons.Default.ArrowDownward,
            iconTint = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))


        SummaryCard(
            title = "Bakiye",
            amount = balance,
            icon = Icons.Default.AccountBalance,
            iconTint = if (balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = formatMoney(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FinancialRecordItem(
    record: FinancialRecord,
    onItemClick: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (record.isIncome)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (record.isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (record.isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = record.category,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatDate(record.date),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (record.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = record.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }


            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatMoney(record.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (record.isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = onDeleteClicked,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialRecordDialog(
    title: String,
    incomeCategories: List<String>,
    expenseCategories: List<String>,
    editingRecord: FinancialRecord? = null,
    onDismiss: () -> Unit,
    onRecordSaved: (title: String, amount: Double, isIncome: Boolean, category: String, description: String) -> Unit,
    onAddCategory: (categoryName: String, isIncome: Boolean) -> Unit
) {
    var recordTitle by remember { mutableStateOf(editingRecord?.title ?: "") }
    var amountText by remember { mutableStateOf(editingRecord?.amount?.toString() ?: "") }
    var isIncome by remember { mutableStateOf(editingRecord?.isIncome ?: true) }
    var category by remember { mutableStateOf(editingRecord?.category ?: "") }
    var description by remember { mutableStateOf(editingRecord?.description ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }


    val currentCategories = if (isIncome) incomeCategories else expenseCategories


    val isFormValid = recordTitle.isNotBlank() && amountText.isNotBlank() && category.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {

                OutlinedTextField(
                    value = recordTitle,
                    onValueChange = { recordTitle = it },
                    label = { Text("Başlık") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Title, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = isIncome,
                        onClick = {
                            isIncome = true
                            category = ""
                        }
                    )
                    Text(
                        text = "Gelir",
                        modifier = Modifier.clickable {
                            isIncome = true
                            category = ""
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = !isIncome,
                        onClick = {
                            isIncome = false
                            category = ""
                        }
                    )
                    Text(
                        text = "Gider",
                        modifier = Modifier.clickable {
                            isIncome = false
                            category = ""
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Tutar (TL)") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))


                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = androidx.compose.material3.MenuAnchorType.PrimaryNotEditable),
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Kategori") },
                        leadingIcon = {
                            Icon(Icons.Default.Category, contentDescription = null)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {

                        currentCategories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                }
                            )
                        }


                        if (currentCategories.isNotEmpty()) {
                            HorizontalDivider()
                        }


                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Yeni Kategori Ekle")
                                }
                            },
                            onClick = {
                                categoryExpanded = false
                                showAddCategoryDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama (İsteğe bağlı)") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onRecordSaved(recordTitle, amount, isIncome, category, description)
                    }
                },
                enabled = isFormValid
            ) {
                Text(if (editingRecord != null) "Güncelle" else "Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )


    if (showAddCategoryDialog) {
        AddCategoryDialog(
            isIncome = isIncome,
            onDismiss = { showAddCategoryDialog = false },
            onCategoryAdded = { categoryName ->
                onAddCategory(categoryName, isIncome)
                category = categoryName
                showAddCategoryDialog = false
            }
        )
    }
}

@Composable
fun AddCategoryDialog(
    isIncome: Boolean,
    onDismiss: () -> Unit,
    onCategoryAdded: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Yeni ${if (isIncome) "Gelir" else "Gider"} Kategorisi") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Kategori Adı") },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Category, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onCategoryAdded(categoryName.trim()) },
                enabled = categoryName.trim().isNotBlank()
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

fun formatMoney(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(amount)
}

fun formatDate(date: java.util.Date): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
    return formatter.format(date)
} 