package com.example.everywrite.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.everywrite.ui.viewmodels.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NotesViewModel,
    onBack: () -> Unit
) {
    val currentNote by viewModel.currentNote.collectAsState()
    var title by remember { mutableStateOf(currentNote?.title ?: "") }
    var content by remember { mutableStateOf(currentNote?.content ?: "") }
    var location by remember { mutableStateOf(currentNote?.location ?: "London") }
    var imageUrl by remember { mutableStateOf(currentNote?.imageUrl ?: "") }
    var weatherPreview by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val popularCities = listOf("London", "Paris", "New York", "Tokyo", "Sydney", "Berlin", "Rome", "Madrid")

    LaunchedEffect(currentNote) {
        title = currentNote?.title ?: ""
        content = currentNote?.content ?: ""
        location = currentNote?.location ?: "London"
        imageUrl = currentNote?.imageUrl ?: ""
    }

    LaunchedEffect(location) {
        if (location.isNotEmpty()) {
            coroutineScope.launch {
                weatherPreview = viewModel.getWeatherPreview(location)
            }
        } else {
            weatherPreview = ""
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (currentNote == null) "🆕 New Note" else "✏️ Edit Note"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isNotEmpty() || content.isNotEmpty()) {
                                viewModel.createNoteWithImage(title, content, imageUrl, location)
                            }
                            onBack()
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }

                    if (currentNote != null) {
                        IconButton(
                            onClick = {
                                currentNote?.let { viewModel.deleteNote(it) }
                                onBack()
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Image URL section
            Text(
                text = "🖼️ Add Image URL",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("🔗 Image URL") },
                placeholder = { Text("https://example.com/image.jpg") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Image, contentDescription = "Image")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Show image preview
            if (imageUrl.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "👀 Image Preview:",
                    style = MaterialTheme.typography.labelMedium
                )
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Note image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location section
            Text(
                text = "📍 Add Location Context",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Quick city buttons
            LazyRow(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularCities) { city ->
                    FilterChip(
                        selected = location == city,
                        onClick = { location = city },
                        label = { Text(city) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Custom location input
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("🌍 City Name (optional)") },
                placeholder = { Text("Enter any city name...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // Show weather preview
            if (weatherPreview.isNotEmpty()) {
                Text(
                    text = "🌤️ Preview: $weatherPreview",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("📌 Title") },
                placeholder = { Text("What's on your mind?") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 20.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content field
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("📄 Content") },
                placeholder = { Text("Start writing your thoughts here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(fontSize = 16.sp),
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}