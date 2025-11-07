package com.example.everywrite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class Reminder(
    val id: String,
    val noteTitle: String,
    val reminderTime: Long, // Store as timestamp
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(onBack: () -> Unit) {
    var reminders by remember { mutableStateOf(emptyList<Reminder>()) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    // Sample reminders (in real app, load from database)
    LaunchedEffect(Unit) {
        reminders = listOf(
            Reminder("1", "Meeting Notes", System.currentTimeMillis() + 3600000), // 1 hour from now
            Reminder("2", "Shopping List", System.currentTimeMillis() + 86400000), // 1 day from now
            Reminder("3", "Project Ideas", System.currentTimeMillis() + 172800000) // 2 days from now
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "⏰ Reminders",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddReminderDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⏰",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "No Reminders Yet",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Add reminders to your notes to stay organized! 🎯",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders) { reminder ->
                        ReminderItem(
                            reminder = reminder,
                            onDelete = {
                                reminders = reminders.filter { it.id != reminder.id }
                            },
                            onToggleComplete = {
                                reminders = reminders.map {
                                    if (it.id == reminder.id) it.copy(isCompleted = !it.isCompleted)
                                    else it
                                }
                            }
                        )
                    }
                }
            }
        }

        // Add Reminder Dialog
        if (showAddReminderDialog) {
            AddReminderDialog(
                onDismiss = { showAddReminderDialog = false },
                onAddReminder = { noteTitle, reminderTime ->
                    val newReminder = Reminder(
                        id = System.currentTimeMillis().toString(),
                        noteTitle = noteTitle,
                        reminderTime = reminderTime
                    )
                    reminders = reminders + newReminder
                    showAddReminderDialog = false
                }
            )
        }
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "📝 ${reminder.noteTitle}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "⏰ ${formatReminderTime(reminder.reminderTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Show status
                Text(
                    text = if (reminder.isCompleted) "✅ Completed" else "🟡 Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reminder.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                )
            }

            // Action buttons
            Row {
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = if (reminder.isCompleted) "Mark pending" else "Mark complete",
                        tint = if (reminder.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAddReminder: (String, Long) -> Unit
) {
    var noteTitle by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "➕ Add Reminder",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("📝 Note Title") },
                    placeholder = { Text("Enter note title to remind...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "📅 Reminder Time: ${formatDateTime(selectedDate, selectedHour, selectedMinute)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Note: In a real app, you'd use Date/Time pickers here
                Text(
                    "⏰ Set a specific time for your reminder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteTitle.isNotEmpty()) {
                        // Calculate timestamp from date + time
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDate
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                        }
                        onAddReminder(noteTitle, calendar.timeInMillis)
                    }
                },
                enabled = noteTitle.isNotEmpty()
            ) {
                Text("💾 Save Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("❌ Cancel")
            }
        }
    )
}

// Helper function to format reminder time
private fun formatReminderTime(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Date()

    return when {
        timestamp < System.currentTimeMillis() -> "⏰ Past due"
        timestamp - System.currentTimeMillis() < 3600000 -> "⏰ Within hour" // 1 hour
        timestamp - System.currentTimeMillis() < 86400000 -> "⏰ Today" // 1 day
        timestamp - System.currentTimeMillis() < 172800000 -> "⏰ Tomorrow" // 2 days
        else -> SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(date)
    }
}

// Helper function to format date and time
private fun formatDateTime(date: Long, hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = date
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(calendar.time)
}