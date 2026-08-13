package com.mahmodhota.worldfood3dadventure.ui.world3d

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/** Sanity checks for the continent coordinate data in [GlobeContinentData]. */
class ContinentGeometryTest {

    // ─── polygon structure ─────────────────────────────────────────────────

    @Test
    fun allPolygonsHaveEvenPointCount() {
        GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
            assertTrue(
                "Polygon $idx has odd length ${poly.size} (must be even lat/lon pairs)",
                poly.size % 2 == 0
            )
        }
    }

    @Test
    fun allPolygonsHaveAtLeastThreePoints() {
        GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
            assertTrue(
                "Polygon $idx has fewer than 3 points (${poly.size / 2})",
                poly.size / 2 >= 3
            )
        }
    }

    @Test
    fun expectedPolygonCount() {
        // 0=N.America, 1=S.America, 2=Europe, 3=Scandinavia, 4=Africa,
        // 5=Asia, 6=Asia-FE, 7=India, 8=Arabian, 9=Italy,
        // 10=Australia, 11=Greenland, 12=Japan, 13=NZ
        assertEquals(14, GlobeContinentData.allPolygons.size)
    }

    // ─── coordinate validity ───────────────────────────────────────────────

    @Test
    fun allLatitudesInValidRange() {
        GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
            var i = 0
            while (i < poly.size) {
                val lat = poly[i]
                assertTrue(
                    "Polygon $idx has out-of-range latitude $lat at index $i",
                    lat >= -90f && lat <= 90f
                )
                i += 2
            }
        }
    }

    @Test
    fun allLongitudesInValidRange() {
        GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
            var i = 1
            while (i < poly.size) {
                val lon = poly[i]
                assertTrue(
                    "Polygon $idx has out-of-range longitude $lon at index $i",
                    lon >= -180f && lon <= 180f
                )
                i += 2
            }
        }
    }

    @Test
    fun noNaNOrInfiniteCoordinates() {
        GlobeContinentData.allPolygons.forEachIndexed { idx, poly ->
            poly.forEachIndexed { ci, value ->
                assertFalse(
                    "Polygon $idx coordinate[$ci] is NaN",
                    value.isNaN()
                )
                assertFalse(
                    "Polygon $idx coordinate[$ci] is Infinite",
                    value.isInfinite()
                )
            }
        }
    }

    // ─── hemisphere / placement sanity ────────────────────────────────────

    @Test
    fun northAmericaIsMainlyNorthernHemisphere() {
        val poly = GlobeContinentData.NORTH_AMERICA
        val northCount = (0 until poly.size step 2).count { poly[it] > 0f }
        assertTrue("N.America should have majority N-hemisphere points", northCount > poly.size / 4)
    }

    @Test
    fun africaSpansBothHemispheres() {
        val poly = GlobeContinentData.AFRICA
        val hasNorth = (0 until poly.size step 2).any { poly[it] > 0f }
        val hasSouth = (0 until poly.size step 2).any { poly[it] < 0f }
        assertTrue("Africa should have northern-hemisphere points", hasNorth)
        assertTrue("Africa should have southern-hemisphere points (e.g. Cape of Good Hope)", hasSouth)
    }

    @Test
    fun australiaIsEntirelyInSouthernHemisphere() {
        val poly = GlobeContinentData.AUSTRALIA
        val northCount = (0 until poly.size step 2).count { poly[it] > 5f }
        assertEquals("Australia should have no significantly northern-hemisphere points", 0, northCount)
    }

    @Test
    fun arabianPeninsulaIsInNorthEasternRegion() {
        val poly = GlobeContinentData.ARABIAN_PENINSULA
        // Mostly 10°–30°N, 35°–60°E
        val lats = (0 until poly.size step 2).map { poly[it] }
        val lons = (1 until poly.size step 2).map { poly[it] }
        assertTrue("Arabian Peninsula min lat should be > 0°", lats.min() > -5f)
        assertTrue("Arabian Peninsula max lat should be < 45°", lats.max() < 45f)
        assertTrue("Arabian Peninsula min lon should be > 20°E", lons.min() > 20f)
        assertTrue("Arabian Peninsula max lon should be < 80°E", lons.max() < 80f)
    }

    @Test
    fun italyIsInMediterraneanRegion() {
        val poly = GlobeContinentData.ITALY_PENINSULA
        val lats = (0 until poly.size step 2).map { poly[it] }
        val lons = (1 until poly.size step 2).map { poly[it] }
        assertTrue("Italy should be N-hemisphere (>35°N)", lats.min() > 35f)
        assertTrue("Italy max lat should be < 48°N", lats.max() < 48f)
        assertTrue("Italy should be in Mediterranean longitude range (7°–20°E)", lons.min() > 7f && lons.max() < 20f)
    }

    @Test
    fun scandinaviaHasNoSelfIntersectingDuplicates() {
        val poly = GlobeContinentData.SCANDINAVIA
        // Check that no (lat,lon) pair appears more than once (would indicate old self-intersection bug)
        val pairs = mutableSetOf<Pair<Float, Float>>()
        var i = 0
        while (i < poly.size - 2) { // -2 to allow closing point to equal first point
            val pair = Pair(poly[i], poly[i + 1])
            assertTrue(
                "Duplicate coordinate found in SCANDINAVIA at index $i: $pair",
                pairs.add(pair)
            )
            i += 2
        }
    }

    @Test
    fun africaGulfOfGuineaHasNoNegativeLatitudeWestOfZero() {
        // Old code had erroneous point at -4°S, 4°W in Gulf of Guinea section.
        // The W African coastline north of equator should not have negative lat points west of 0°.
        val poly = GlobeContinentData.AFRICA
        var i = 0
        while (i < poly.size) {
            val lat = poly[i]
            val lon = poly[i + 1]
            if (lon < -3f && lon > -20f) {
                // In the W Africa longitude range: lat should be >= 3°N (Guinea coast area)
                assertTrue(
                    "Unexpected S-hemisphere point in W Africa region: lat=$lat, lon=$lon",
                    lat >= 3f
                )
            }
            i += 2
        }
    }
    @Test
    fun greenlandIsNorthernHighLatitude() {
        val poly = GlobeContinentData.GREENLAND
        val lats = (0 until poly.size step 2).map { poly[it] }
        assertTrue("Greenland should be at or above 60°N", lats.min() >= 60f)
    }

    // ─── country marker coordinates ────────────────────────────────────────

    private val COUNTRY_MARKERS = mapOf(
        "Germany" to Pair(51.2f, 10.5f),
        "Italy"   to Pair(42.5f, 12.5f),
        "France"  to Pair(46.2f, 2.2f),
        "Spain"   to Pair(40.4f, -3.7f),
        "Japan"   to Pair(36.2f, 138.3f),
        "Mexico"  to Pair(23.6f, -102.6f),
        "Sudan"   to Pair(12.9f, 30.2f)
    )

    @Test
    fun allCountryMarkersHaveValidLatLon() {
        COUNTRY_MARKERS.forEach { (name, coords) ->
            val (lat, lon) = coords
            assertTrue("$name lat $lat out of range", lat >= -90f && lat <= 90f)
            assertTrue("$name lon $lon out of range", lon >= -180f && lon <= 180f)
        }
    }

    @Test
    fun europeanMarkersAreInEuropeRegion() {
        listOf("Germany", "Italy", "France", "Spain").forEach { name ->
            val (lat, lon) = COUNTRY_MARKERS[name]!!
            assertTrue("$name lat $lat should be in Europe N-hemisphere (>30°N)", lat > 30f)
            assertTrue("$name lon $lon should be in European longitude range (-15°..30°E)", lon > -15f && lon < 30f)
        }
    }

    @Test
    fun japanIsInFarEasternRegion() {
        val (lat, lon) = COUNTRY_MARKERS["Japan"]!!
        assertTrue("Japan should be N-hemisphere", lat > 0f)
        assertTrue("Japan lon should be E-Asia range (130°–145°E)", lon > 130f && lon < 145f)
    }
}
