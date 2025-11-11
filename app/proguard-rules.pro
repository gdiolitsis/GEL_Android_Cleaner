# ===== GEL Cleaner Base Rules =====

# Keep app classes
-keep class com.gel.** { *; }

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Google Material
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Prevent warnings
-dontwarn org.apache.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep native access
-keep class * extends java.lang.Exception
