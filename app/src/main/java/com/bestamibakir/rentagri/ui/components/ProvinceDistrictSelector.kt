package com.bestamibakir.rentagri.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bestamibakir.rentagri.data.model.TurkeyData
import com.bestamibakir.rentagri.ui.theme.RentAgriTheme


@Composable
fun ProvinceSelector(
    selectedProvince: String,
    onProvinceSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "İl",
    isError: Boolean = false,
    errorMessage: String = "",
    isEnabled: Boolean = true
) {

    val provinces = TurkeyData.getAllProvinceNames()

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        Box {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { if (isEnabled) expanded = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedProvince.ifEmpty { "İl seçiniz" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedProvince.isEmpty())
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                provinces.forEach { province ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = province,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onProvinceSelected(province)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun DistrictSelector(
    selectedDistrict: String,
    selectedProvince: String,
    onDistrictSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "İlçe",
    isError: Boolean = false,
    errorMessage: String = "",
    isEnabled: Boolean = true
) {

    val districts = TurkeyData.getDistrictsByProvince(selectedProvince)
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        Box {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (isEnabled && districts.isNotEmpty()) {
                        expanded = true
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            selectedProvince.isEmpty() -> "Önce il seçiniz"
                            districts.isEmpty() -> "Bu il için ilçe bilgisi yok"
                            selectedDistrict.isEmpty() -> "İlçe seçiniz"
                            else -> selectedDistrict
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedDistrict.isEmpty() || selectedProvince.isEmpty())
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (districts.isNotEmpty()) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    districts.forEach { district ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = district,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onDistrictSelected(district)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ProvinceDistrictSelector(
    selectedProvince: String,
    selectedDistrict: String,
    onProvinceSelected: (String) -> Unit,
    onDistrictSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isProvinceError: Boolean = false,
    isDistrictError: Boolean = false,
    provinceErrorMessage: String = "",
    districtErrorMessage: String = "",
    isEnabled: Boolean = true
) {
    Column(modifier = modifier) {
        ProvinceSelector(
            selectedProvince = selectedProvince,
            onProvinceSelected = { province ->
                onProvinceSelected(province)

                if (selectedDistrict.isNotEmpty()) {
                    onDistrictSelected("")
                }
            },
            isError = isProvinceError,
            errorMessage = provinceErrorMessage,
            isEnabled = isEnabled
        )

        Spacer(modifier = Modifier.height(16.dp))

        DistrictSelector(
            selectedDistrict = selectedDistrict,
            selectedProvince = selectedProvince,
            onDistrictSelected = onDistrictSelected,
            isError = isDistrictError,
            errorMessage = districtErrorMessage,
            isEnabled = isEnabled
        )
    }
}

@Preview(name = "Light Mode")
@Composable
fun ProvinceDistrictSelectorPreview() {
    RentAgriTheme {
        ProvinceDistrictSelector(
            selectedProvince = "İstanbul",
            selectedDistrict = "Kadıköy",
            onProvinceSelected = {},
            onDistrictSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProvinceDistrictSelectorDarkPreview() {
    RentAgriTheme {
        ProvinceDistrictSelector(
            selectedProvince = "",
            selectedDistrict = "",
            onProvinceSelected = {},
            onDistrictSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
} 