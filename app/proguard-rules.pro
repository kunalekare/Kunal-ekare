# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Preserve Firebase and Room data models and entities
-keep class com.example.core.model.** { *; }
-keep class com.example.core.data.local.db.** { *; }
-keepclassmembers class com.example.core.model.** {
    public <init>(...);
    <fields>;
}

# Preserve Room generated code
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Preserve Media3 ExoPlayer components
-keep class androidx.media3.** { *; }

# WorkManager
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
