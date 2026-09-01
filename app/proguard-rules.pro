# Project-specific R8 rules for release.
# Keep rules are intentionally minimal; Compose/Firebase/DataStore rely on
# their published consumer rules from dependencies.

# Entry point initialized from AndroidManifest.xml.
-keep class com.mahmodhota.worldfood3dadventure.WorldFoodAdventureApplication { <init>(); }

# Filament 3D Engine
-keep class com.google.android.filament.** { *; }
-keep class com.google.android.filament.utils.** { *; }
-keep class com.google.android.filament.gltfio.** { *; }

