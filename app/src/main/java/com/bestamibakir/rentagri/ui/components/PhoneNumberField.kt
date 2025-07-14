package com.bestamibakir.rentagri.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import com.bestamibakir.rentagri.data.model.Country
import com.bestamibakir.rentagri.data.model.CountryData
import com.bestamibakir.rentagri.ui.theme.RentAgriTheme

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    selectedCountry: Country,
    modifier: Modifier = Modifier,
    label: String = "Telefon",
    isError: Boolean = false,
    errorMessage: String = "",
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = "${selectedCountry.flag} ${selectedCountry.phoneCode}",
                onValueChange = { },
                enabled = false,
                modifier = Modifier.width(120.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->

                    val filteredValue = newValue.filter { it.isDigit() }

                    val maxLength = selectedCountry.phoneLength.maxOrNull() ?: 15
                    if (filteredValue.length <= maxLength) {
                        onValueChange(filteredValue)
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = selectedCountry.phoneFormat)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = imeAction
                ),
                isError = isError,
                singleLine = true
            )
        }

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        } else {

            val lengthText = if (selectedCountry.phoneLength.count() == 1) {
                "${selectedCountry.phoneLength.first()} hane"
            } else {
                "${selectedCountry.phoneLength.min()}-${selectedCountry.phoneLength.max()} hane"
            }
            Text(
                text = "Format: ${selectedCountry.phoneFormat} ($lengthText)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(name = "Light Mode")
@Composable
fun PhoneNumberFieldPreview() {
    RentAgriTheme {
        PhoneNumberField(
            value = "5551234567",
            onValueChange = {},
            selectedCountry = CountryData.supportedCountries.first { it.code == "TR" },
            label = "Telefon Numarası"
        )
    }
}

@Preview(name = "With Error", showBackground = true)
@Composable
fun PhoneNumberFieldErrorPreview() {
    RentAgriTheme {
        PhoneNumberField(
            value = "123",
            onValueChange = {},
            selectedCountry = CountryData.supportedCountries.first { it.code == "US" },
            label = "Phone Number",
            isError = true,
            errorMessage = "Invalid phone format. Example: 2125551234"
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PhoneNumberFieldDarkPreview() {
    RentAgriTheme {
        PhoneNumberField(
            value = "123456789",
            onValueChange = {},
            selectedCountry = CountryData.supportedCountries.first { it.code == "FR" },
            label = "Numéro de téléphone"
        )
    }
} 