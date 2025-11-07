package com.example.everywrite.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@Composable
fun SafeComposable(
    modifier: Modifier = Modifier,
    showErrorScreen: Boolean = true,
    content: @Composable () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var errorStackTrace by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Catch any async errors (e.g., ViewModel flow crashes)
    val handler = remember {
        CoroutineExceptionHandler { _, throwable ->
            Log.e("SafeComposable", "Caught exception in UI: ${throwable.message}", throwable)
            hasError = true
            errorMessage = throwable.message ?: "An unexpected error occurred"
            errorStackTrace = throwable.stackTraceToString()

            // Log detailed error for debugging
            Log.d("SafeComposable", "Full stack trace: $errorStackTrace")

            scope.launch {
                Toast.makeText(
                    context,
                    "⚠️ App encountered an error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        // Keep the handler alive in case async code throws
        scope.launch(handler) {
            // This coroutine will stay alive to catch errors
        }
    }

    if (!hasError) {
        // Render your composable normally
        content()
    } else if (showErrorScreen) {
        // Show a beautiful fallback screen instead of crashing
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "😅 Oops! Something went wrong",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                // Simple debug info (always show shortened version)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Error details: ${errorMessage}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            hasError = false
                            errorMessage = ""
                            errorStackTrace = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Try Again")
                    }

                    OutlinedButton(
                        onClick = {
                            // Show more detailed error info
                            scope.launch {
                                Toast.makeText(
                                    context,
                                    "Check Logcat for full error details",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {
                        Text("More Info")
                    }
                }

                // Helpful tip
                Text(
                    text = "💡 If this keeps happening, try restarting the app",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // If showErrorScreen is false, just show empty state
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Content unavailable",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}