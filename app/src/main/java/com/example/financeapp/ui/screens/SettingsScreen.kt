package com.example.financeapp.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val useDynamicColors by viewModel.useDynamicColors.collectAsState()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()

    var showCurrencyDialog by remember { mutableStateOf(false) }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentSymbol = currencySymbol,
            onDismiss = { showCurrencyDialog = false },
            onSelect = { viewModel.setCurrencySymbol(it) }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            item {
                SettingsCard(title = "Appearance") {
                    SettingsSwitchItem(
                        title = "Dark Mode",
                        icon = Icons.Outlined.NightsStay,
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                    HorizontalDivider()
                    SettingsSwitchItem(
                        title = "Dynamic Colors",
                        subtitle = "Use wallpaper colors (Android 12+)",
                        icon = Icons.Outlined.ColorLens,
                        checked = useDynamicColors,
                        onCheckedChange = { viewModel.setDynamicColors(it) },
                        enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    )
                }
            }

            // General Section
            item {
                SettingsCard(title = "General") {
                    SettingsClickableItem(
                        title = "Currency",
                        subtitle = "Current: $currencySymbol",
                        icon = Icons.Outlined.Public,
                        onClick = { showCurrencyDialog = true }
                    )
                }
            }
            
            // Security Section
             item {
                SettingsCard(title = "Security") {
                    SettingsSwitchItem(
                        title = "Biometric Unlock",
                        subtitle = "Use Fingerprint/Face ID",
                        icon = Icons.Outlined.Fingerprint,
                        checked = isBiometricEnabled,
                        onCheckedChange = { viewModel.setBiometricEnabled(it) }
                    )
                }
            }

            // Data Section
            item {
                SettingsCard(title = "Data") {
                     SettingsClickableItem(
                        title = "Export Data",
                        subtitle = "Save transactions as CSV",
                        icon = Icons.Outlined.SaveAlt,
                        onClick = { Toast.makeText(context, "Exporting data... (Simulation)", Toast.LENGTH_SHORT).show() }
                    )
                    HorizontalDivider()
                    SettingsClickableItem(
                        title = "Clear All Data",
                        subtitle = "This cannot be undone",
                        icon = Icons.Outlined.Delete,
                        isDestructive = true,
                        onClick = { Toast.makeText(context, "Data cleared (Simulation)", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            // About Section
            item {
                SettingsCard(title = "About") {
                    SettingsClickableItem(
                        title = "Version",
                        subtitle = "1.0.0",
                        icon = Icons.Outlined.Info,
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )
            content()
        }
    }
}

@Composable
private fun CurrencySelectionDialog(currentSymbol: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            Column {
                listOf("$" to "USD", "€" to "EUR", "£" to "GBP", "¥" to "JPY").forEach { (symbol, code) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                onSelect(symbol)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = symbol == currentSymbol,
                            onClick = { 
                                onSelect(symbol)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("$symbol ($code)")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = if (subtitle != null) { { Text(subtitle) } } else null,
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled) },
        modifier = Modifier.clickable(enabled = enabled) { onCheckedChange(!checked) },
        colors = if (!enabled) ListItemDefaults.colors(
            disabledHeadlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ) else ListItemDefaults.colors()
    )
}

@Composable
private fun SettingsClickableItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val itemColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    ListItem(
        headlineContent = { Text(text = title, color = itemColor) },
        supportingContent = if (subtitle != null) { { Text(subtitle) } } else null,
        leadingContent = { 
            Icon(
                icon, 
                contentDescription = null,
                tint = if (isDestructive) itemColor else MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
