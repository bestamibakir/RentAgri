package com.bestamibakir.rentagri.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MakeOfferDialog(
    listingTitle: String,
    listingPrice: Double,
    onDismiss: () -> Unit,
    onMakeOffer: (amount: Double, message: String) -> Unit
) {
    var offerAmount by remember { mutableStateOf("") }
    var offerMessage by remember { mutableStateOf("") }
    var isAmountError by remember { mutableStateOf(false) }

    val formatPrice = { price: Double ->
        NumberFormat.getCurrencyInstance(Locale("tr", "TR")).format(price)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Teklif Ver",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = listingTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mevcut Fiyat: ${formatPrice(listingPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = offerAmount,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
                            offerAmount = value
                            isAmountError = false
                        }
                    },
                    label = { Text("Teklif Miktarı (₺)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isAmountError,
                    supportingText = if (isAmountError) {
                        { Text("Geçerli bir miktar giriniz") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = offerMessage,
                    onValueChange = { offerMessage = it },
                    label = { Text("Mesaj (Opsiyonel)") },
                    placeholder = { Text("Teklifinizle birlikte bir mesaj yazabilirsiniz...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = offerAmount.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        onMakeOffer(amount, offerMessage.trim())
                    } else {
                        isAmountError = true
                    }
                }
            ) {
                Text("Teklif Ver")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
} 