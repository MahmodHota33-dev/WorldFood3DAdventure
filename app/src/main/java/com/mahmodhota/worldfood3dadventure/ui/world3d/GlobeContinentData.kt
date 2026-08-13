package com.mahmodhota.worldfood3dadventure.ui.world3d

/**
 * Continent outlines for the 3D globe — Phase 10.3D polish pass.
 *
 * Each entry is a [FloatArray] of interleaved (latitude°, longitude°) pairs.
 * Clockwise winding.  No polygon crosses ±180° (avoids antimeridian artefacts).
 *
 * Phase 10.3D changes vs 10.3C:
 *  • SCANDINAVIA: fixed self-intersecting duplicate points — polygon now correctly
 *    traces W coast northward then E coast southward.
 *  • AFRICA: significantly more intermediate samples on N coast, W coast / Gulf of
 *    Guinea, E coast, and Horn region — eliminates long straight edges.
 *  • EUROPE_MAIN: more points on Iberian Med coast, Bay of Biscay, Adriatic/Aegean.
 *  • INDIA: expanded to ~31 points for clearly recognisable peninsula shape.
 *  • ASIA_MAIN: India connection now routed along Himalayan foothills (no more
 *    horizontal cut through India); more SE Asia / Malay Peninsula detail.
 *  • ARABIAN_PENINSULA: more Gulf of Aden / Oman coast detail.
 *  • ITALY_PENINSULA: new polygon — gives the "boot" shape in Mediterranean views.
 *  • allPolygons: 14 entries (was 13).  Italy at index 9; Australia→10, Greenland→11,
 *    Japan→12, NZ→13.
 */
object GlobeContinentData {

    /** North America — Alaska → Canada → USA → Mexico → Central America. */
    val NORTH_AMERICA = floatArrayOf(
        // Arctic / NW Alaska coast
        71f,-157f,  72f,-142f,  73f,-120f,  74f,-100f,
        72f, -84f,  70f, -80f,
        // Labrador / NE Canada coast
        63f, -64f,  60f, -64f,  57f, -63f,  53f, -56f,
        // Newfoundland
        47f, -53f,  47f, -56f,  46f, -61f,
        // Nova Scotia / Atlantic coast
        44f, -66f,  42f, -70f,  41f, -73f,
        // US East Coast
        40f, -74f,  38f, -75f,  36f, -76f,  34f, -77f,
        32f, -81f,
        // Florida peninsula
        29f, -81f,  27f, -80f,  25f, -80f,  25f, -81f,
        26f, -82f,  29f, -83f,  30f, -84f,
        // Gulf of Mexico coast
        29f, -89f,  29f, -90f,  28f, -91f,  28f, -94f,
        26f, -97f,  24f, -98f,
        // Mexico east coast + Yucatán
        22f, -98f,  20f, -87f,  21f, -87f,
        // Central America isthmus
        17f, -88f,  16f, -89f,  13f, -87f,  11f, -85f,
         9f, -83f,   9f, -79f,   8f, -77f,
        // Pacific coast going NW
        10f, -85f,  13f, -89f,  15f, -93f,  16f, -97f,
        20f,-105f,  23f,-110f,  28f,-114f,
        // US West Coast
        32f,-117f,  34f,-120f,  37f,-122f,  40f,-124f,
        44f,-124f,  48f,-124f,
        // British Columbia
        50f,-127f,  53f,-130f,  56f,-133f,  58f,-137f,
        59f,-140f,  60f,-141f,
        // Alaska south coast
        60f,-146f,  59f,-152f,  59f,-153f,  57f,-153f,
        56f,-157f,  55f,-163f,
        // Alaska peninsula
        57f,-170f,  60f,-166f,  63f,-163f,  65f,-166f,
        66f,-168f,  68f,-166f,  70f,-162f,  71f,-157f
    )

    /** South America — Colombia coast clockwise, Brazil NE bulge. */
    val SOUTH_AMERICA = floatArrayOf(
        // Caribbean N coast
        11f, -74f,  12f, -72f,  11f, -64f,   9f, -60f,
         8f, -60f,   7f, -61f,   6f, -61f,   5f, -58f,
         4f, -53f,   2f, -50f,
        // Brazil NE bulge
         1f, -50f,  -2f, -43f,  -5f, -35f,  -8f, -35f,
        -10f, -37f, -13f, -39f, -16f, -39f, -20f, -40f,
        -23f, -43f, -28f, -48f, -33f, -52f,
        // Argentina
        -35f, -57f, -38f, -57f, -42f, -63f,
        -47f, -65f, -52f, -68f,
        // Cape Horn / Tierra del Fuego
        -55f, -67f, -56f, -68f, -55f, -71f, -54f, -71f,
        // Chile W coast going north
        -49f, -75f, -44f, -74f, -38f, -73f, -33f, -71f,
        -28f, -71f, -23f, -70f, -18f, -71f, -16f, -75f,
        -12f, -77f,
        // Peru / Ecuador / Colombia
         -8f, -81f,  -3f, -81f,   0f, -80f,   1f, -80f,
          4f, -77f,   8f, -77f,  11f, -74f
    )

    /**
     * Europe mainland — Gibraltar clockwise via Mediterranean, Black Sea, Russia,
     * Scandinavia, Atlantic / Iberian coast.
     *
     * Phase 10.3D: more points on Iberian Med coast, Bay of Biscay,
     * Adriatic/Balkan/Greek coast, and Aegean transition.
     */
    val EUROPE_MAIN = floatArrayOf(
        // ── S coast: Gibraltar → Spain Med → French Riviera → Ligurian ─────
        36f, -6f,   36f, -4f,   37f, -1f,   38f,  0f,
        38f,  1f,   39f,  1f,   40f,  0f,   40f,  2f,
        41f,  2f,   42f,  3f,   42f,  4f,   43f,  4f,
        43f,  5f,   43f,  6f,   43f,  7f,   44f,  8f,
        44f,  9f,
        // ── NE Italy base / Trieste / Istria ──────────────────────────────
        45f, 12f,   45f, 13f,   45f, 14f,   44f, 15f,
        // ── Adriatic E coast / Balkans / Greece ───────────────────────────
        43f, 16f,   42f, 17f,   41f, 18f,   40f, 19f,
        39f, 20f,   38f, 21f,   37f, 22f,   37f, 23f,
        37f, 25f,   37f, 26f,   37f, 28f,   38f, 27f,
        // ── Turkey / Aegean → Black Sea ───────────────────────────────────
        40f, 27f,   40f, 28f,   41f, 29f,
        // ── Black Sea N coast ─────────────────────────────────────────────
        42f, 28f,   43f, 29f,   44f, 28f,   45f, 29f,
        46f, 31f,   46f, 34f,   46f, 37f,
        44f, 37f,   43f, 40f,   43f, 47f,
        // ── NE Europe / Russia inland boundary ────────────────────────────
        47f, 47f,   50f, 43f,   53f, 40f,   55f, 37f,
        57f, 32f,   59f, 30f,   63f, 28f,   65f, 26f,
        // ── North Cape ────────────────────────────────────────────────────
        71f, 28f,   70f, 25f,
        // ── Norway W coast going S ────────────────────────────────────────
        69f, 18f,   68f, 18f,   66f, 15f,   65f, 14f,
        63f, 10f,   61f,  8f,   59f,  5f,
        // ── S Norway / Denmark / Jutland ──────────────────────────────────
        57f,  8f,   56f,  9f,   55f, 10f,   54f,  9f,
        // ── N German / Netherlands / Belgium / N France ───────────────────
        53f,  8f,   53f,  7f,   53f,  5f,   52f,  4f,
        51f,  3f,   51f,  2f,   50f,  2f,
        // ── Brittany peninsula ────────────────────────────────────────────
        49f,  0f,   49f, -2f,   48f, -4f,   48f, -5f,
        47f, -3f,   47f, -2f,
        // ── Bay of Biscay S / Basque / Cantabrian coast ───────────────────
        46f, -2f,   45f, -2f,   44f, -2f,   43f, -2f,
        43f, -3f,   43f, -5f,   43f, -7f,   43f, -8f,
        // ── Galicia / N Portugal ──────────────────────────────────────────
        42f, -8f,   42f, -9f,   41f, -9f,
        // ── Portugal W coast ──────────────────────────────────────────────
        40f, -9f,   39f, -9f,   38f,-10f,
        // ── Cape St. Vincent / SW tip ─────────────────────────────────────
        37f, -9f,   36f, -9f,   36f, -8f,   36f, -7f,   36f, -6f
    )

    /**
     * Scandinavian peninsula.
     *
     * Phase 10.3D: completely redrawn — old polygon had self-intersecting duplicate
     * points.  Now correctly traces W coast northward then E/SE coast southward.
     */
    val SCANDINAVIA = floatArrayOf(
        // ── S: Gothenburg / Skaggerak ─────────────────────────────────────
        57f, 12f,   58f, 11f,   59f, 10f,
        // ── W Norway coast going N ────────────────────────────────────────
        59f,  5f,   60f,  5f,   62f,  6f,
        63f,  8f,   65f, 14f,   67f, 16f,
        68f, 18f,   69f, 18f,   70f, 20f,
        70f, 25f,   71f, 28f,
        // ── E coast going S (Sweden / Finland border) ─────────────────────
        69f, 28f,   67f, 24f,   64f, 22f,
        62f, 20f,   60f, 18f,   58f, 16f,
        57f, 12f
    )

    /**
     * Africa — clockwise from N Morocco.
     *
     * Phase 10.3D: major coastline polish — significantly more samples on N coast,
     * Red Sea, Horn of Africa, E coast, S Africa, W coast / Gulf of Guinea,
     * Liberia/Guinea/Senegal W bulge, Morocco Atlantic.
     * Corrected Gulf of Guinea section (previous had erroneous -4°S point).
     */
    val AFRICA = floatArrayOf(
        // ── N coast: Morocco → Algeria → Tunisia → Libya → Egypt ──────────
        35f, -6f,   36f, -2f,   36f,  3f,   37f,  7f,
        37f,  9f,   36f, 11f,   33f, 12f,   33f, 14f,
        32f, 17f,   32f, 20f,   31f, 24f,   31f, 28f,
        31f, 32f,
        // ── Sinai / Red Sea W coast ───────────────────────────────────────
        29f, 33f,   28f, 34f,   27f, 35f,   26f, 36f,
        24f, 37f,   22f, 37f,   20f, 38f,   18f, 40f,
        17f, 41f,   16f, 41f,   15f, 42f,   14f, 43f,
        13f, 43f,   12f, 44f,
        // ── Horn of Africa (Somalia protrudes to ~51°E) ───────────────────
        12f, 45f,   13f, 47f,   14f, 49f,   12f, 51f,
        11f, 51f,   10f, 51f,    9f, 50f,    7f, 48f,
         5f, 47f,    4f, 43f,    3f, 45f,    2f, 45f,
         1f, 41f,   -1f, 40f,
        // ── E Africa coast ────────────────────────────────────────────────
        -3f, 40f,   -5f, 39f,   -7f, 40f,  -10f, 40f,
       -12f, 40f,  -14f, 40f,  -16f, 37f,  -18f, 36f,
       -20f, 35f,  -23f, 35f,  -25f, 34f,  -27f, 33f,
       -29f, 31f,
        // ── South Africa / Cape of Good Hope ──────────────────────────────
       -31f, 30f,  -33f, 28f,  -34f, 27f,  -34f, 26f,
       -35f, 24f,  -35f, 22f,  -34f, 20f,  -34f, 18f,
       -32f, 18f,  -30f, 17f,  -28f, 16f,
        // ── Namibia / Angola W coast going N ─────────────────────────────
       -25f, 15f,  -22f, 14f,  -19f, 12f,  -15f, 12f,
       -12f, 14f,   -8f, 13f,   -5f, 12f,   -3f, 11f,
        -2f, 10f,   -1f, 10f,
        // ── Gulf of Guinea: Gabon → Cameroon → Nigeria → Ghana ───────────
         0f,  9f,    1f,  9f,    3f,  9f,    4f,  9f,
         5f,  7f,    5f,  5f,    5f,  3f,    6f,  2f,
         6f,  3f,    5f,  1f,    5f,  0f,    5f, -1f,
         5f, -3f,    5f, -5f,    4f, -7f,    4f, -8f,
        // ── Liberia / Sierra Leone ────────────────────────────────────────
         5f,-10f,    6f,-11f,    7f,-12f,    8f,-13f,
        // ── Guinea / Senegal W bulge (westernmost Africa ~15°N,-17°W) ─────
         9f,-14f,   10f,-14f,   11f,-15f,   12f,-16f,
        13f,-16f,   14f,-17f,   15f,-17f,   16f,-17f,
        // ── Mauritania going N ────────────────────────────────────────────
        18f,-16f,   20f,-17f,   22f,-17f,   23f,-16f,
        25f,-15f,   27f,-14f,
        // ── Morocco Atlantic ──────────────────────────────────────────────
        29f,-12f,   30f,-10f,   32f, -9f,   33f, -8f,   35f, -6f
    )

    /**
     * Asia mainland — Caucasus east to Siberia, south to SE Asia.
     *
     * Phase 10.3D:
     *  • India connection re-routed along Himalayan foothills (was a horizontal
     *    straight cut at 22°N through India — now follows 26–27°N).
     *  • More intermediate points through Myanmar / Malay Peninsula / Vietnam.
     */
    val ASIA_MAIN = floatArrayOf(
        // ── Start: Caucasus / Caspian ─────────────────────────────────────
        43f, 47f,   43f, 53f,   38f, 55f,   36f, 58f,
        // ── Afghanistan / Pakistan → N India (Himalayan foothills) ────────
        31f, 62f,   28f, 64f,   26f, 68f,   26f, 72f,
        27f, 79f,   27f, 85f,   25f, 90f,   24f, 92f,
        // ── Bangladesh / Myanmar ──────────────────────────────────────────
        22f, 92f,   20f, 92f,
        // ── Myanmar / Irrawaddy Delta ─────────────────────────────────────
        17f, 95f,   16f, 97f,   16f, 98f,   15f, 98f,
        // ── Thailand / Malay Peninsula W coast ────────────────────────────
        13f,100f,   11f, 99f,    9f, 99f,    7f,100f,
         5f,101f,    3f,102f,    2f,103f,    1f,104f,
        // ── E coast of Malay going N ──────────────────────────────────────
         2f,104f,    4f,104f,    6f,103f,    8f,103f,
        // ── Indochina / Vietnam coast ─────────────────────────────────────
        10f,107f,   11f,108f,   12f,109f,   14f,109f,
        16f,108f,   18f,107f,   20f,107f,
        // ── S China coast / South China Sea ───────────────────────────────
        22f,114f,   22f,120f,   23f,120f,   24f,120f,
        // ── E China coast / East China Sea ────────────────────────────────
        27f,121f,   30f,122f,   32f,122f,   34f,120f,
        // ── Yellow Sea / Shandong ─────────────────────────────────────────
        36f,121f,   37f,122f,   38f,122f,   39f,122f,
        // ── Korean Peninsula ──────────────────────────────────────────────
        34f,126f,   35f,129f,   37f,129f,   38f,129f,
        // ── N Korea / Manchuria ───────────────────────────────────────────
        42f,130f,   43f,131f,   46f,136f,   48f,140f,
        // ── E Russia coast going N ────────────────────────────────────────
        52f,141f,   56f,138f,   60f,140f,   63f,141f,   65f,142f,
        // ── Siberia Arctic coast going W ──────────────────────────────────
        68f,141f,   70f,130f,   72f,100f,
        72f, 80f,   72f, 55f,   72f, 28f,
        // ── W Siberia going SW ────────────────────────────────────────────
        68f, 50f,   62f, 55f,   60f, 60f,
        57f, 65f,   55f, 73f,   50f, 83f,
        // ── Central Asia ──────────────────────────────────────────────────
        48f, 88f,   43f, 88f,   41f, 80f,
        38f, 75f,   37f, 70f,   38f, 65f,
        40f, 60f,   42f, 55f,   41f, 50f,
        41f, 42f,
        // ── Turkey / Anatolia N coast → Black Sea ─────────────────────────
        38f, 40f,   37f, 36f,   40f, 36f,   41f, 29f,
        42f, 28f,
        // ── Return via Caucasus ───────────────────────────────────────────
        43f, 41f,   43f, 47f
    )

    /** Siberian Far East — separate polygon to avoid antimeridian crossing. */
    val ASIA_FAR_EAST = floatArrayOf(
        68f,141f,   68f,160f,   65f,172f,   60f,162f,
        55f,163f,   50f,140f,   52f,141f,   56f,138f,
        60f,140f,   63f,141f,   65f,142f,   68f,141f
    )

    /**
     * Indian subcontinent — Phase 10.3D: expanded to ~31 pts.
     * More Malabar (W) coast and Coromandel (E) coast points give clearly
     * recognisable tapering peninsula.
     */
    val INDIA = floatArrayOf(
        // ── NW: Kutch / Gujarat coast ─────────────────────────────────────
        23f,  68f,   22f,  70f,   21f,  72f,
        // ── W (Malabar) coast going S ─────────────────────────────────────
        20f,  73f,   18f,  74f,   16f,  74f,
        14f,  74f,   12f,  75f,   10f,  76f,
         9f,  77f,    8f,  77f,
        // ── Southern tip (Cape Comorin ~8°N,77°E) ─────────────────────────
         8f,  78f,    8f,  79f,    8f,  80f,
        // ── E (Coromandel) coast going N ─────────────────────────────────
         9f,  80f,   10f,  80f,   12f,  80f,
        14f,  80f,   15f,  80f,   16f,  81f,
        17f,  82f,   19f,  85f,   20f,  87f,
        22f,  90f,
        // ── NE corner (Bangladesh) ────────────────────────────────────────
        23f,  91f,   24f,  90f,   25f,  88f,
        // ── N border (Himalayan foot) going W ─────────────────────────────
        27f,  88f,   28f,  84f,   29f,  80f,
        30f,  76f,   31f,  74f,   30f,  71f,
        27f,  69f,   25f,  68f,   23f,  68f
    )

    /**
     * Arabian Peninsula — Phase 10.3D: more Gulf of Aden and Oman coast detail.
     */
    val ARABIAN_PENINSULA = floatArrayOf(
        // ── NW: Syria / Lebanon coast ─────────────────────────────────────
        35f, 36f,   33f, 36f,   30f, 35f,   28f, 34f,
        // ── Red Sea W coast going S ───────────────────────────────────────
        24f, 38f,   22f, 39f,   19f, 41f,   17f, 42f,
        15f, 43f,   13f, 43f,   12f, 44f,   12f, 45f,
        // ── S coast / Gulf of Aden going E ────────────────────────────────
        13f, 48f,   14f, 50f,   14f, 51f,   15f, 52f,
        16f, 53f,
        // ── Oman / Cape of Arabia ─────────────────────────────────────────
        17f, 55f,   18f, 57f,   20f, 58f,   22f, 60f,
        23f, 59f,
        // ── Gulf of Oman / Strait of Hormuz ───────────────────────────────
        24f, 57f,   25f, 57f,   26f, 57f,
        // ── Persian Gulf (UAE / Qatar / Kuwait) ───────────────────────────
        24f, 55f,   23f, 52f,   24f, 51f,
        27f, 50f,   29f, 49f,   30f, 48f,
        // ── Return NW to Syria ────────────────────────────────────────────
        32f, 36f,   35f, 36f
    )

    /**
     * Italian peninsula — NEW in Phase 10.3D.
     *
     * The "boot" shape is one of the most recognisable geographic features visible
     * from the Mediterranean / Africa view.  Clockwise from Ligurian NW coast.
     */
    val ITALY_PENINSULA = floatArrayOf(
        // ── NW: Genoa / Ligurian coast ────────────────────────────────────
        44f,  8f,   43f,  9f,   43f, 10f,
        // ── W (Tyrrhenian) coast going S ──────────────────────────────────
        42f, 11f,   41f, 13f,   40f, 14f,
        39f, 16f,
        // ── Calabria toe ──────────────────────────────────────────────────
        38f, 16f,   38f, 15f,
        // ── Heel / Taranto gulf ───────────────────────────────────────────
        39f, 17f,   40f, 18f,   41f, 17f,
        // ── Adriatic coast going N ────────────────────────────────────────
        42f, 15f,   43f, 13f,
        // ── NE corner (Trieste) back to NW ────────────────────────────────
        45f, 14f,   45f, 12f,   44f,  9f,   44f,  8f
    )

    /**
     * Australia — clockwise from NW Darwin area.
     * Gulf of Carpentaria and Cape York Peninsula preserved.
     */
    val AUSTRALIA = floatArrayOf(
        // NW coast / Darwin
        -12f,130f,  -11f,132f,  -12f,134f,  -12f,136f,
        // Gulf of Carpentaria W side
        -14f,136f,  -17f,136f,
        // Gulf base → E side
        -17f,139f,  -14f,140f,
        // Cape York Peninsula
        -12f,141f,  -10f,142f,
        // E coast going S
        -12f,143f,  -16f,146f,  -20f,148f,  -24f,152f,
        -28f,153f,  -32f,152f,  -38f,147f,
        // SE corner
        -39f,147f,  -39f,143f,  -38f,141f,  -37f,140f,
        // S coast / Great Australian Bight
        -36f,137f,  -33f,134f,  -32f,128f,  -32f,122f,
        // WA coast
        -26f,113f,  -22f,114f,
        // NW coast back to Darwin
        -17f,122f,  -14f,126f,  -13f,130f,  -12f,130f
    )

    /** Greenland — clockwise. */
    val GREENLAND = floatArrayOf(
        76f,-68f,  80f,-55f,  83f,-35f,  83f,-27f,
        78f,-18f,  74f,-20f,  70f,-22f,  65f,-38f,
        60f,-44f,  60f,-50f,  63f,-55f,  67f,-55f,
        71f,-53f,  76f,-68f
    )

    /** Japan — Honshu main island. */
    val JAPAN_HONSHU = floatArrayOf(
        31f,131f,  33f,130f,  34f,132f,  35f,136f,
        35f,137f,  36f,138f,  37f,136f,  37f,138f,
        38f,141f,  40f,142f,  41f,141f,  42f,142f,
        43f,141f,  44f,143f,  44f,145f,  43f,145f,
        40f,143f,  38f,141f,  35f,139f,  33f,132f,
        31f,131f
    )

    val NEW_ZEALAND_NORTH = floatArrayOf(
        -34f,172f, -37f,175f, -37f,176f, -39f,177f,
        -41f,175f, -41f,174f, -38f,174f, -36f,174f,
        -34f,172f
    )

    /**
     * All polygon shapes in rendering order.
     * Index MUST match CONTINENT_FILLS / CONTINENT_COASTS / CONTINENT_MOUNTAIN_TINT in GlobeRenderer.
     *
     * 0=N.America, 1=S.America, 2=Europe, 3=Scandinavia, 4=Africa,
     * 5=Asia, 6=Asia-FE, 7=India, 8=Arabian, 9=Italy,
     * 10=Australia, 11=Greenland, 12=Japan, 13=NZ
     */
    val allPolygons: List<FloatArray> = listOf(
        NORTH_AMERICA, SOUTH_AMERICA,
        EUROPE_MAIN, SCANDINAVIA,
        AFRICA,
        ASIA_MAIN, ASIA_FAR_EAST, INDIA, ARABIAN_PENINSULA,
        ITALY_PENINSULA,
        AUSTRALIA, GREENLAND, JAPAN_HONSHU, NEW_ZEALAND_NORTH
    )
}

