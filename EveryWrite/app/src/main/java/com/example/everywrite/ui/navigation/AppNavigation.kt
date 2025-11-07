package com.example.everywrite.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.everywrite.ui.auth.LoginScreen
import com.example.everywrite.ui.auth.SignupScreen
import com.example.everywrite.ui.screens.*
import androidx.compose.material3.MaterialTheme
import com.example.everywrite.ui.viewmodels.AuthViewModel
import com.example.everywrite.ui.viewmodels.NotesViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    notesViewModel: NotesViewModel,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onClearCache: () -> Unit
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    var navigationError by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Clear error when navigating
    LaunchedEffect(navController) {
        // This will clear errors when navigation succeeds
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                navigationError = null
            },
            title = { Text("🚨 Navigation Error") },
            text = {
                Column {
                    Text("Could not navigate to the requested screen.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Error: ${navigationError ?: "Unknown error"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This might be because the screen isn't properly set up in navigation.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                        navigationError = null
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Safe navigation function
    fun safeNavigate(route: String, actionName: String) {
        try {
            Log.d("Navigation", "Attempting to navigate to: $route")
            navController.navigate(route) {
                // Add navigation options if needed
                launchSingleTop = true
            }
            Log.d("Navigation", "✅ Successfully navigated to: $route")
        } catch (e: Exception) {
            navigationError = "Failed to navigate to $route: ${e.message}"
            showErrorDialog = true
            Log.e("Navigation", "❌ Navigation failed for $route: ${e.message}", e)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "notes" else "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { safeNavigate("notes", "Login Success") },
                onNavigateToSignup = { safeNavigate("signup", "Navigate to Signup") },
                onBack = { /* Can't go back from login */ }
            )
        }

        composable("signup") {
            SignupScreen(
                authViewModel = authViewModel,
                onSignupSuccess = { safeNavigate("notes", "Signup Success") },
                onNavigateToLogin = { safeNavigate("login", "Navigate to Login") },
                onBack = {
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        navigationError = "Failed to go back: ${e.message}"
                        showErrorDialog = true
                    }
                }
            )
        }

        composable("notes") {
            NotesScreen(
                viewModel = notesViewModel,
                authViewModel = authViewModel,
                onNoteClick = { note ->
                    try {
                        notesViewModel.setCurrentNote(note)
                        safeNavigate("noteDetail", "Open Note Detail")
                    } catch (e: Exception) {
                        navigationError = "Failed to open note: ${e.message}"
                        showErrorDialog = true
                    }
                },
                onAddNote = {
                    try {
                        notesViewModel.createNewNote()
                        safeNavigate("noteDetail", "Create New Note")
                    } catch (e: Exception) {
                        navigationError = "Failed to create note: ${e.message}"
                        showErrorDialog = true
                    }
                },
                onNavigateToReminders = {
                    safeNavigate("reminders", "Open Reminders")
                },
                onNavigateToSettings = {
                    safeNavigate("settings", "Open Settings")
                },
                onNavigateToReviews = {
                    safeNavigate("reviews", "Open Reviews")
                },
                onLogout = {
                    try {
                        authViewModel.logout()
                        safeNavigate("login", "Logout")
                    } catch (e: Exception) {
                        navigationError = "Logout failed: ${e.message}"
                        showErrorDialog = true
                    }
                }
            )
        }

        composable("reviews") {
            ReviewsScreen(
                onBack = {
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        navigationError = "Failed to go back from reviews: ${e.message}"
                        showErrorDialog = true
                    }
                }
            )
        }

        composable("noteDetail") {
            NoteDetailScreen(
                viewModel = notesViewModel,
                onBack = {
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        navigationError = "Failed to go back from note detail: ${e.message}"
                        showErrorDialog = true
                    }
                }
            )
        }

        composable("reminders") {
            RemindersScreen(
                onBack = {
                    try {
                        navController.popBackStack()
                    } catch (e: Exception) {
                        navigationError = "Failed to go back from reminders: ${e.message}"
                        showErrorDialog = true
                    }
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBack = {
                    try {
                        navController.popBackStack()
                        Log.d("Navigation", "✅ Going back from settings")
                    } catch (e: Exception) {
                        navigationError = "Failed to go back from settings: ${e.message}"
                        showErrorDialog = true
                        Log.e("Navigation", "❌ Back navigation failed from settings", e)
                    }
                },
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onClearCache = onClearCache,
                notesViewModel = notesViewModel // ✅ ADDED THIS LINE - Fixes the missing parameter
            )
        }
    }
}