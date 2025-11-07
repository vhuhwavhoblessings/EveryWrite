// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false  // CHANGED from 1.9.0 to 1.9.20
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false  // CHANGED ksp version
}