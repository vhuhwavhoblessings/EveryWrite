package com.example.everywrite.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import com.example.everywrite.utils.NotificationHelper

class NotesRepository(private val noteDao: NoteDao, private val context: Context) {

    private val notificationHelper = NotificationHelper(context)

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    fun getArchivedNotes(): Flow<List<Note>> = noteDao.getArchivedNotes()
    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    suspend fun getNoteById(id: String): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
        // Try to show notification, but don't fail if we can't
        val title = "📝 Note Added"
        val message = "Your note '${note.title.take(20)}${if (note.title.length > 20) "..." else ""}' has been saved!"
        notificationHelper.showNotification(title, message)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
        // Try to show notification, but don't fail if we can't
        val title = "🗑️ Note Deleted"
        val message = "Note '${note.title.take(20)}${if (note.title.length > 20) "..." else ""}' has been deleted"
        notificationHelper.showNotification(title, message)
    }

    suspend fun deleteAllArchived() = noteDao.deleteAllArchived()

    suspend fun updatePinStatus(id: String, isPinned: Boolean) = noteDao.updatePinStatus(id, isPinned)

    suspend fun updateArchiveStatus(id: String, isArchived: Boolean) = noteDao.updateArchiveStatus(id, isArchived)

    suspend fun createNoteWithWeather(
        title: String,
        content: String,
        city: String = "London"
    ): Note {
        val (weatherInfo, weatherIcon) = getSimpleWeatherForCity(city)

        val note = Note(
            title = title,
            content = content,
            location = city,
            weather = weatherInfo,
            weatherIcon = weatherIcon,
            updatedAt = System.currentTimeMillis()
        )

        insertNote(note)
        return note
    }

    suspend fun createNoteWithImage(
        title: String,
        content: String,
        imageUrl: String? = null,
        city: String = "London"
    ): Note {
        val (weatherInfo, weatherIcon) = getSimpleWeatherForCity(city)

        val note = Note(
            title = title,
            content = content,
            location = city,
            weather = weatherInfo,
            weatherIcon = weatherIcon,
            imageUrl = imageUrl,
            updatedAt = System.currentTimeMillis()
        )

        insertNote(note)
        return note
    }

    private fun getSimpleWeatherForCity(city: String): Pair<String, String> {
        val cityWeather = mapOf(
            "london" to Pair("🌧️ Rainy, 15°C", "🌧️"),
            "paris" to Pair("⛅ Cloudy, 18°C", "⛅"),
            "new york" to Pair("☀️ Sunny, 22°C", "☀️"),
            "tokyo" to Pair("☀️ Sunny, 25°C", "☀️"),
            "sydney" to Pair("☀️ Sunny, 28°C", "☀️"),
            "berlin" to Pair("⛅ Cloudy, 16°C", "⛅"),
            "rome" to Pair("☀️ Sunny, 24°C", "☀️"),
            "madrid" to Pair("☀️ Sunny, 26°C", "☀️"),
            "amsterdam" to Pair("🌧️ Rainy, 14°C", "🌧️"),
            "dublin" to Pair("🌧️ Rainy, 13°C", "🌧️"),
            "moscow" to Pair("❄️ Snowy, -5°C", "❄️"),
            "dubai" to Pair("☀️ Sunny, 35°C", "☀️"),
            "los angeles" to Pair("☀️ Sunny, 26°C", "☀️"),
            "toronto" to Pair("⛅ Cloudy, 12°C", "⛅"),
            "singapore" to Pair("🌧️ Rainy, 30°C", "🌧️")
        )

        return cityWeather[city.lowercase()] ?: Pair("🌈 Beautiful, 20°C", "🌈")
    }

    suspend fun getQuickWeather(city: String): String {
        return getSimpleWeatherForCity(city).first
    }

    // NEW: Check if notifications are enabled
    fun canShowNotifications(): Boolean {
        return notificationHelper.canShowNotifications()
    }
}