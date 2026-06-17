# Project ProGuard rules.

# Preserve stack traces for crash reporting (Phase 2 wires Crashlytics).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.radhaarc.dicerollar.**$$serializer { *; }
-keepclassmembers class com.radhaarc.dicerollar.** {
    *** Companion;
}
-keepclasseswithmembers class com.radhaarc.dicerollar.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Compose runtime keeps itself; nothing else needed for v1.
