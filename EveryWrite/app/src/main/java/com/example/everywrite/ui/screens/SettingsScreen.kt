package com.example.everywrite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.everywrite.ui.viewmodels.NotesViewModel
import com.example.everywrite.utils.NotificationPermissionHelper
import com.example.everywrite.utils.RequestNotificationPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onClearCache: () -> Unit,
    notesViewModel: NotesViewModel? = null  // ✅ ADD THIS PARAMETER
) {
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionHelper = remember { NotificationPermissionHelper(context) }
    val languages = listOf("English", "Spanish", "French", "German", "Japanese", "Chinese", "Korean")

    // Check initial permission status
    LaunchedEffect(Unit) {
        notificationPermissionGranted = permissionHelper.hasNotificationPermission()
        notificationsEnabled = notificationPermissionGranted
    }

    // Handle notification permission request
    RequestNotificationPermission(
        onPermissionGranted = {
            notificationPermissionGranted = true
            notificationsEnabled = true
        },
        onPermissionDenied = {
            notificationPermissionGranted = false
            notificationsEnabled = false
        },
        onShowRationale = {
            showPermissionDialog = true
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "⚙️ Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Language Section
            SettingsSection(title = "🌐 Language & Region") {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "App Language",
                    subtitle = "Change app display language",
                    value = currentLanguage,
                    onClick = { showLanguageDialog = true }
                )
            }

            // Appearance Section
            SettingsSection(title = "🎨 Appearance") {
                SettingsSwitchItem(
                    icon = Icons.Default.Palette,
                    title = "Dark Mode",
                    subtitle = "Switch between light and dark theme",
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChange
                )
            }

            // Notifications Section
            SettingsSection(title = "🔔 Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Enable Notifications",
                    subtitle = "Get notified when notes are added or deleted",
                    checked = notificationsEnabled,
                    onCheckedChange = { newValue ->
                        if (newValue && !notificationPermissionGranted) {
                            // User wants to enable notifications but no permission
                            // The RequestNotificationPermission will trigger automatically
                            notificationsEnabled = true
                        } else if (!newValue) {
                            // User wants to disable notifications
                            notificationsEnabled = false
                        }
                        // If user wants to enable and already has permission, just update the state
                    }
                )

                // Show permission status
                if (!notificationPermissionGranted && notificationsEnabled) {
                    Text(
                        text = "⏳ Waiting for permission...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                } else if (!notificationPermissionGranted) {
                    Text(
                        text = "🔒 Notifications require permission",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                } else {
                    Text(
                        text = "✅ Notifications enabled",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                }
            }

            // Data & Storage Section
            SettingsSection(title = "💾 Data & Storage") {
                SettingsButtonItem(
                    icon = Icons.Default.Storage,
                    title = "Clear Cache",
                    subtitle = "Remove archived notes and free up space",
                    buttonText = "Clear Now",
                    onClick = { showClearCacheDialog = true }
                )
            }

            // About Section
            SettingsSection(title = "ℹ️ About") {
                SettingsItem(
                    title = "📱 Version",
                    value = "1.0.0",
                    onClick = { }
                )
                SettingsItem(
                    title = "👨‍💻 Developer",
                    value = "EveryWrite Team",
                    onClick = { }
                )
                SettingsItem(
                    title = "📧 Support",
                    value = "Contact us",
                    onClick = { }
                )
            }

            // App Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📝 EveryWrite",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Your thoughts, beautifully organized",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "🌟 Stay organized with smart notes 🌟",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Permission Rationale Dialog
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = {
                    showPermissionDialog = false
                    notificationsEnabled = false
                },
                title = {
                    Text(
                        "🔔 Enable Notifications",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column {
                        Text(
                            "Notifications help you:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("• 📝 Get notified when notes are saved", style = MaterialTheme.typography.bodySmall)
                        Text("• 🗑️ Know when notes are deleted", style = MaterialTheme.typography.bodySmall)
                        Text("• 🔔 Stay updated with your writing", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Please allow notification permission to use this feature.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showPermissionDialog = false }
                    ) {
                        Text("✅ Understand")
                    }
                }
            )
        }

        // Language Selection Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = {
                    Text(
                        "🌐 Select Language",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column {
                        languages.forEach { language ->
                            RadioButtonItem(
                                text = language,
                                selected = currentLanguage == language,
                                onSelect = {
                                    onLanguageChange(language)
                                    showLanguageDialog = false
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("✅ OK")
                    }
                }
            )
        }

        // Clear Cache Confirmation Dialog
        if (showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = { showClearCacheDialog = false },
                title = {
                    Text(
                        "🗑️ Clear Cache",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column {
                        Text("This will:")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 🗂️ Permanently delete all archived notes", style = MaterialTheme.typography.bodySmall)
                        Text("• 💾 Free up storage space", style = MaterialTheme.typography.bodySmall)
                        Text("• 🚫 Cannot be undone", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Are you sure you want to continue?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearCacheDialog = false
                            onClearCache()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("🚀 Clear Now")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearCacheDialog = false }
                    ) {
                        Text("❌ Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SettingsButtonItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 16.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.height(36.dp)
        ) {
            Text(buttonText)
        }
    }
}

@Composable
fun RadioButtonItem(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}