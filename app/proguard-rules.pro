# ============================================================
# GEL Android Cleaner — ProGuard / R8 Rules
# Hospital-Grade Edition (30 LABS + Manual Tests)
# ============================================================

# ===== Keep ALL app classes (Activities, Services, Logs, Diagnostics) =====
-keep class com.gel.** { *; }

# ===== AndroidX =====
-keep class androidx.** { *; }
-dontwarn androidx.**

# ===== Google Material =====
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ===== Prevent warnings from optional libraries =====
-dontwarn org.apache.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ===== Keep Exceptions =====
-keep class * extends java.lang.Exception

# ============================================================
# 30 LABS — CRITICAL INFRASTRUCTURE KEEP RULES
# ============================================================

# --- Reflection (SELinux, System Properties, ABI, getprop) ---
-keep class android.os.SELinux { *; }
-keep class android.os.SystemProperties { *; }
-dontwarn android.os.SystemProperties

# --- exec("getprop") / Runtime / BufferedReader ---
-keep class java.lang.Runtime { *; }
-keep class java.io.BufferedReader { *; }
-keep class java.io.InputStreamReader { *; }

# --- Sensors / SensorManager / Sensor List ---
-keep class android.hardware.Sensor { *; }
-keep class android.hardware.SensorManager { *; }
-dontwarn android.hardware.SensorManager

# --- HardwarePropertiesManager (Thermals) ---
-keep class android.os.HardwarePropertiesManager { *; }
-dontwarn android.os.HardwarePropertiesManager

# --- Battery (ACTION_BATTERY_CHANGED) ---
-keep class android.os.BatteryManager { *; }
-keep class android.content.Intent { *; }
-keep class android.content.IntentFilter { *; }
-dontwarn android.content.IntentFilter

# --- Connectivity / NetworkCapabilities ---
-keep class android.net.ConnectivityManager { *; }
-keep class android.net.NetworkCapabilities { *; }
-keep class android.net.NetworkInfo { *; }

# --- Audio / ToneGenerator / Vibrator ---
-keep class android.media.ToneGenerator { *; }
-keep class android.os.Vibrator { *; }

# --- Camera summary (no stripping of camera manager) ---
-keep class android.hardware.camera2.** { *; }
-dontwarn android.hardware.camera2.**

# --- NFC / GPS / Location ---
-keep class android.nfc.** { *; }
-dontwarn android.nfc.**
-keep class android.location.** { *; }
-dontwarn android.location.**

# --- WindowManager / Display / Metrics ---
-keep class android.view.Display { *; }
-keep class android.util.DisplayMetrics { *; }

# --- GPU Renderer / EGL / OpenGL ES ---
-keep class android.opengl.** { *; }
-dontwarn android.opengl.**

# --- File & Storage I/O (internal/external diagnostics) ---
-keep class java.io.File { *; }

# ============================================================
# Export System LOG (GELServiceLog + PDF/TXT formatting)
# ============================================================
-keep class com.gel.cleaner.GELServiceLog { *; }

# ============================================================
# Keep annotations / prevent stripping logs
# ============================================================
-keepattributes *Annotation*
