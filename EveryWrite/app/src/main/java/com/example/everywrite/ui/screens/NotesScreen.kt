package com.example.everywrite.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.everywrite.ui.viewmodels.AuthViewModel
import com.example.everywrite.ui.viewmodels.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    authViewModel: AuthViewModel,
    onNoteClick: (com.example.everywrite.data.Note) -> Unit,
    onAddNote: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onLogout: () -> Unit
) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var debugInfo by remember { mutableStateOf("") }

    // Debug function to log navigation attempts
    fun logNavigationAttempt(screen: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        debugInfo = "Last click: $screen at $timestamp"
        Log.d("NavigationDebug", "🔄 Attempting to navigate to: $screen")
        Log.d("NavigationDebug", "📊 Notes count: ${notes.size}, User: ${currentUser?.username}")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "📝 EveryWrite",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (currentUser != null) {
                            Text(
                                text = "👋 Welcome, ${currentUser!!.username}!",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Search button
                    IconButton(
                        onClick = {
                            logNavigationAttempt("Search")
                            viewModel.setIsSearching(true)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // More options dropdown
                    Box {
                        IconButton(
                            onClick = {
                                logNavigationAttempt("More Menu")
                                showMoreMenu = true
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            // Reviews
                            DropdownMenuItem(
                                text = { Text("⭐ Reviews") },
                                onClick = {
                                    logNavigationAttempt("Reviews Screen")
                                    showMoreMenu = false
                                    onNavigateToReviews()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Star, contentDescription = null)
                                }
                            )

                            }
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                // Reviews
                                DropdownMenuItem(
                                    text = { Text("⭐ Reviews") },
                                    onClick = {
                                        logNavigationAttempt("Reviews Screen")
                                        showMoreMenu = false
                                        scope.launch {
                                            delay(100)
                                            onNavigateToReviews()
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Star, contentDescription = null)
                                    }
                                )

                                // Reminders
                                DropdownMenuItem(
                                    text = { Text("📁 Reminders") },
                                    onClick = {
                                        logNavigationAttempt("Reminders Screen")
                                        showMoreMenu = false
                                        scope.launch {
                                            delay(100)
                                            onNavigateToReminders()
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Archive, contentDescription = null)
                                    }
                                )

                                // Settings
                                DropdownMenuItem(
                                    text = { Text("⚙️ Settings") },
                                    onClick = {
                                        logNavigationAttempt("Settings Screen")
                                        showMoreMenu = false
                                        scope.launch {
                                            delay(100)
                                            onNavigateToSettings()
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Settings, contentDescription = null)
                                    }
                                )

                                Divider()

                                // Logout
                                DropdownMenuItem(
                                    text = { Text("🚪 Logout") },
                                    onClick = {
                                        logNavigationAttempt("Logout Dialog")
                                        showMoreMenu = false
                                        scope.launch {
                                            delay(100)
                                            showLogoutDialog = true
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                                    }
                                )
                            }

                        }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    logNavigationAttempt("Add Note")
                    onAddNote()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Note",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        // Logout Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("🚪 Logout") },
                text = {
                    Column {
                        Text("Are you sure you want to logout?")
                        Spacer(modifier = Modifier.height(8.dp))
                        if (debugInfo.isNotEmpty()) {
                            Text(
                                text = "Debug: $debugInfo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            logNavigationAttempt("Confirm Logout")
                            showLogoutDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            logNavigationAttempt("Cancel Logout")
                            showLogoutDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Debug info (only visible during testing)
            if (debugInfo.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "🔍 $debugInfo",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2
                    )
                }
            }

            // Search Bar
            if (isSearching) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { },
                    active = isSearching,
                    onActiveChange = { viewModel.setIsSearching(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search your notes...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                ) {
                    // Search results
                }
            }

            // Empty State
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "📝",
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 64.sp
                        )
                        Text(
                            text = "No notes yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap the + button below to create your first note! 🎉",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Notes List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { note ->
                        ColoredNoteItem(
                            note = note,
                            onNoteClick = {
                                logNavigationAttempt("Note: ${note.title}")
                                onNoteClick(note)
                            },
                            onPinClick = {
                                logNavigationAttempt("Pin Note: ${note.title}")
                                viewModel.updatePinStatus(note.id, !note.isPinned)
                            },
                            onArchiveClick = {
                                logNavigationAttempt("Archive Note: ${note.title}")
                                viewModel.updateArchiveStatus(note.id, true)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoredNoteItem(
    note: com.example.everywrite.data.Note,
    onNoteClick: () -> Unit,
    onPinClick: () -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = remember(note.id) {
        generateColorFromId(note.id)
    }

    Card(
        onClick = onNoteClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // WEATHER & LOCATION SECTION
            if (note.weather != null || note.location != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    note.weather?.let { weather ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = weather,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    note.location?.let { location ->
                        Text(
                            text = "📍 $location",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // TITLE SECTION WITH PIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title.ifEmpty { "📄 Untitled" },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (note.isPinned) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CONTENT SECTION
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // FOOTER SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                        .format(Date(note.updatedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPinClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (note.isPinned) Icons.Default.Star else Icons.Default.Star,
                            contentDescription = if (note.isPinned) "Unpin" else "Pin",
                            tint = if (note.isPinned) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onArchiveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// Color generation function
private fun generateColorFromId(id: String): androidx.compose.ui.graphics.Color {
    val colors = listOf(
        androidx.compose.ui.graphics.Color(0xFFFFF9C4), // Light Yellow
        androidx.compose.ui.graphics.Color(0xFFE1F5FE), // Light Blue
        androidx.compose.ui.graphics.Color(0xFFF3E5F5), // Light Purple
        androidx.compose.ui.graphics.Color(0xFFE8F5E8), // Light Green
        androidx.compose.ui.graphics.Color(0xFFFFEBEE), // Light Pink
        androidx.compose.ui.graphics.Color(0xFFFCE4EC), // Light Rose
        androidx.compose.ui.graphics.Color(0xFFE0F2F1), // Light Teal
        androidx.compose.ui.graphics.Color(0xFFFFF3E0), // Light Orange
    )

    // Use note ID to pick a consistent color
    val index = id.hashCode().absoluteValue % colors.size
    return colors[index]
}