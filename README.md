Android app with a api
GitHub Link: 

https://github.com/ST10375407/EveryWrite.git 

YouTube Link: 

https://youtube.com/shorts/OFYsJD5VaVM?si=Z9qbegoAEggwjAqE 

explaination
📝 EveryWrite – Smart Notes App

EveryWrite is a modern Android note-taking app built using Kotlin, MVVM, Room Database, Coroutines, and Flow.

It demonstrates clean architecture, reactive data handling, and feature extensibility.

🛠 Tech Stack

Language: Kotlin

Architecture: MVVM + Repository Pattern

Database: Room

Async Handling: Kotlin Coroutines + Flow

Notifications: Android Notification System

✨ Key Features

Create, update, delete notes

Pin & archive functionality

Real-time search

Local user authentication

Weather-enhanced notes

Image-enhanced notes

Smart notifications on note actions

🧠 How the “API” Works

This project uses a mock weather API simulation inside the NotesRepository.

Instead of calling a real external API, the app:

Accepts a city name (e.g., "London")

Looks it up in a predefined weather map:

private fun getSimpleWeatherForCity(city: String): Pair<String, String>


Returns:

Weather description (e.g., "🌧️ Rainy, 15°C")

Weather icon (emoji)

The repository attaches this data to the Note entity before saving it to Room.

Example flow:

UI → Repository → Mock Weather Lookup → Room Database → Flow → UI updates automatically


Because the weather logic is isolated inside the Repository:

It can easily be replaced with a real REST API (e.g., Retrofit)

The UI layer does not need modification

Business logic remains centralized

This demonstrates:

Clean separation of concerns

Scalable architecture

API-ready design

📌 What This Project Shows

Strong understanding of Room & DAO patterns

Proper use of Flow for reactive updates

Repository-based abstraction

Extensible architecture for future API integration

Developer: Blessings Nemandava
Android Developer | Kotlin | MVVM



 
