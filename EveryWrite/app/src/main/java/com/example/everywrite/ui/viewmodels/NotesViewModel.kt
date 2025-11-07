package com.example.everywrite.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.everywrite.data.Note
import com.example.everywrite.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {

    val notes = repository.getAllNotes()
    val archivedNotes = repository.getArchivedNotes()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _notificationPermissionState = MutableStateFlow(false)
    val notificationPermissionState: StateFlow<Boolean> = _notificationPermissionState.asStateFlow()

    fun searchNotes(query: String) = repository.searchNotes(query)

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun setIsSearching(isSearching: Boolean) { _isSearching.value = isSearching }

    fun setCurrentNote(note: Note?) { _currentNote.value = note }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun updatePinStatus(id: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.updatePinStatus(id, isPinned)
        }
    }

    fun updateArchiveStatus(id: String, isArchived: Boolean) {
        viewModelScope.launch {
            repository.updateArchiveStatus(id, isArchived)
        }
    }

    fun deleteAllArchived() {
        viewModelScope.launch {
            repository.deleteAllArchived()
        }
    }

    fun createNewNote() {
        val newNote = Note(
            title = "",
            content = ""
        )
        setCurrentNote(newNote)
    }

    fun createNoteWithWeather(title: String, content: String, location: String) {
        viewModelScope.launch {
            repository.createNoteWithWeather(title, content, location)
        }
    }

    fun createNoteWithImage(title: String, content: String, imageUrl: String?, location: String) {
        viewModelScope.launch {
            repository.createNoteWithImage(title, content, imageUrl, location)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.deleteAllArchived()
        }
    }

    suspend fun getCacheSize(): String {
        val archivedNotes = repository.getArchivedNotes().first().size
        val estimatedSize = archivedNotes * 2
        return if (estimatedSize < 1024) {
            "${estimatedSize} KB"
        } else {
            "${estimatedSize / 1024} MB"
        }
    }

    suspend fun getWeatherPreview(location: String): String {
        return repository.getQuickWeather(location)
    }

    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }

    // ✅ ADDED: Function to check notification permissions
    fun canShowNotifications(): Boolean {
        return repository.canShowNotifications()
    }

    // ✅ ADDED: Function to update notification permission state
    fun updateNotificationPermissionState(hasPermission: Boolean) {
        _notificationPermissionState.value = hasPermission
    }

    // ✅ ADDED: Function to get notification status for UI
    fun getNotificationStatus(): String {
        return if (canShowNotifications()) {
            "🔔 Notifications enabled"
        } else {
            "🔒 Notifications disabled"
        }
    }

    // ✅ ADDED: Function to handle notification operations safely
    suspend fun safeNotificationOperation(operation: () -> Unit): Boolean {
        return try {
            if (canShowNotifications()) {
                operation()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // ✅ ADDED: Function to refresh notification permission status
    fun refreshNotificationStatus() {
        // This will trigger a re-check of the permission status
        _notificationPermissionState.value = repository.canShowNotifications()
    }
}