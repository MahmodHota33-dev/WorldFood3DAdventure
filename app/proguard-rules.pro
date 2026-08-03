# Project-specific R8 rules for release.
# Keep rules are intentionally minimal; Compose/Firebase/DataStore rely on
# their published consumer rules from dependencies.

# Entry point initialized from AndroidManifest.xml.
-keep class com.mahmodhota.worldfood3dadventure.WorldFoodAdventureApplication { <init>(); }

