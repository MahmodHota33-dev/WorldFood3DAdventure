package com.mahmodhota.worldfood3dadventure.ui.world3d

/**
 * Original simplified continent outlines for the experimental 3D globe.
 *
 * Each entry is a [FloatArray] of interleaved (latitude°, longitude°) pairs
 * forming a closed polygon.  Geographic coordinates are factual data and are
 * not subject to copyright.  The specific simplified polygon shapes were
 * created from scratch for this project's stylised globe.
 *
 * Design constraints:
 *  • No polygon crosses the ±180° meridian (avoids wrap-around artefacts).
 *  • Point counts are intentionally low (~20–30 per shape) for fast projection.
 */
object GlobeContinentData {

    val NORTH_AMERICA = floatArrayOf(
        71f, -156f,  72f, -131f,  70f,  -95f,  70f,  -80f,
        65f,  -65f,  52f,  -55f,  47f,  -53f,  44f,  -66f,
        42f,  -70f,  40f,  -74f,  35f,  -76f,  30f,  -81f,
        25f,  -80f,  24f,  -83f,  20f,  -88f,  16f,  -90f,
        15f,  -87f,  20f, -105f,  23f, -110f,  29f, -114f,
        32f, -117f,  37f, -122f,  48f, -124f,  54f, -130f,
        60f, -139f,  66f, -168f,  71f, -156f
    )

    val SOUTH_AMERICA = floatArrayOf(
        12f,  -72f,  11f,  -62f,   8f,  -60f,   5f,  -52f,
         2f,  -50f,  -3f,  -36f,  -8f,  -35f, -10f,  -37f,
       -15f,  -39f, -23f,  -43f, -33f,  -53f, -38f,  -57f,
       -42f,  -63f, -45f,  -65f, -55f,  -68f, -53f,  -70f,
       -45f,  -73f, -38f,  -74f, -33f,  -71f, -18f,  -70f,
        -5f,  -81f,   0f,  -80f,   5f,  -77f,   8f,  -77f,
        10f,  -75f,  12f,  -72f
    )

    val EUROPE_MAIN = floatArrayOf(
        36f,  -9f,  36f,  3f,  40f,  3f,  44f,  8f,
        44f,  14f,  46f, 14f,  46f, 17f,  47f, 22f,
        44f,  29f,  41f, 29f,  38f, 27f,  41f, 20f,
        39f,  22f,  37f, 24f,  36f, 28f,  40f, 36f,
        41f,  41f,  43f, 41f,  44f, 47f,  47f, 47f,
        51f,  40f,  55f, 37f,  59f, 30f,  60f, 25f,
        60f,  20f,  63f, 20f,  65f, 15f,  63f,  9f,
        58f,   5f,  52f,  5f,  51f,  3f,  50f,  2f,
        47f,   2f,  45f,  0f,  43f, -9f,  36f, -9f
    )

    val SCANDINAVIA = floatArrayOf(
        71f,  28f,  70f, 18f,  65f, 14f,  58f,  5f,
        57f,   8f,  57f, 12f,  55f, 12f,  55f,  9f,
        57f,   9f,  58f, 12f,  63f,  8f,  65f, 15f,
        68f,  19f,  70f, 25f,  71f, 28f
    )

    val AFRICA = floatArrayOf(
        37f,  10f,  31f, 32f,  22f, 37f,  12f, 43f,
        11f,  45f,   8f, 48f,   4f, 41f,   2f, 41f,
        -4f,  40f, -10f, 40f, -17f, 38f, -25f, 35f,
       -34f,  28f, -35f, 20f, -34f, 18f, -29f, 16f,
       -22f,  15f, -18f, 12f, -15f, 12f, -10f, 15f,
        -5f,  10f,   4f,  2f,   5f,  2f,   6f,  1f,
        10f,   1f,  16f, -5f,  21f,-17f,  26f,-13f,
        33f,  -8f,  37f, 10f
    )

    /** Large Asia polygon; deliberately ends before the 180° meridian. */
    val ASIA_MAIN = floatArrayOf(
        72f,  28f,  72f, 55f,  72f, 80f,  72f,100f,
        70f, 130f,  68f,141f,  65f,142f,  60f,140f,
        55f, 136f,  50f,140f,  45f,135f,  40f,130f,
        38f, 120f,  35f,120f,  25f,120f,  20f,120f,
        10f, 100f,   5f,100f,   0f,105f,  -5f,108f,
        -8f, 115f,  -5f,105f,   5f,103f,  10f, 98f,
        15f,  97f,  20f, 93f,  23f, 88f,   8f, 80f,
         8f,  77f,  13f, 74f,  23f, 68f,  24f, 62f,
        25f,  58f,  24f, 55f,  18f, 50f,  13f, 45f,
        15f,  42f,  18f, 42f,  29f, 34f,  37f, 36f,
        38f,  40f,  41f, 42f,  41f, 50f,  42f, 55f,
        40f,  60f,  38f, 65f,  37f, 70f,  38f, 75f,
        41f,  80f,  43f, 88f,  48f, 88f,  50f, 83f,
        55f,  73f,  57f, 65f,  60f, 60f,  62f, 55f,
        68f,  50f,  72f, 40f,  72f, 28f
    )

    /** Siberian Far-East cape (~140°–172°E) as a separate shape. */
    val ASIA_FAR_EAST = floatArrayOf(
        68f, 141f,  68f,160f,  65f,172f,  60f,162f,
        55f, 163f,  50f,140f,  55f,136f,  60f,140f,
        65f, 142f,  68f,141f
    )

    val INDIA = floatArrayOf(
        23f,  68f,  22f, 70f,  20f, 73f,  14f, 74f,
         8f,  77f,   8f, 80f,  10f, 80f,  13f, 80f,
        16f,  82f,  20f, 87f,  22f, 90f,  23f, 91f,
        26f,  89f,  27f, 88f,  29f, 80f,  33f, 75f,
        29f,  71f,  26f, 68f,  23f, 68f
    )

    val AUSTRALIA = floatArrayOf(
       -15f, 129f, -12f,136f, -14f,136f, -17f,140f,
       -18f, 146f, -20f,148f, -24f,152f, -28f,153f,
       -32f, 152f, -38f,147f, -38f,145f, -40f,145f,
       -38f, 140f, -36f,137f, -32f,134f, -32f,115f,
       -26f, 113f, -22f,114f, -18f,122f, -14f,127f,
       -15f, 129f
    )

    val GREENLAND = floatArrayOf(
        76f, -68f,  83f,-35f,  83f,-28f,  76f,-19f,
        70f, -22f,  65f,-38f,  60f,-43f,  60f,-50f,
        63f, -55f,  66f,-55f,  70f,-53f,  76f,-68f
    )

    val JAPAN_HONSHU = floatArrayOf(
        31f, 131f,  33f,130f,  34f,132f,  35f,136f,
        35f, 137f,  36f,138f,  37f,136f,  38f,141f,
        40f, 142f,  41f,141f,  42f,142f,  43f,141f,
        44f, 143f,  44f,145f,  43f,145f,  40f,143f,
        38f, 141f,  35f,139f,  33f,132f,  31f,131f
    )

    val NEW_ZEALAND_NORTH = floatArrayOf(
       -34f, 172f, -37f,175f, -37f,176f, -39f,177f,
       -41f, 175f, -41f,174f, -38f,174f, -36f,174f,
       -34f, 172f
    )

    /** All polygon shapes for the globe renderer. */
    val allPolygons: List<FloatArray> = listOf(
        NORTH_AMERICA, SOUTH_AMERICA,
        EUROPE_MAIN, SCANDINAVIA,
        AFRICA,
        ASIA_MAIN, ASIA_FAR_EAST, INDIA,
        AUSTRALIA, GREENLAND, JAPAN_HONSHU, NEW_ZEALAND_NORTH
    )
}
