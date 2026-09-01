package com.mahmodhota.worldfood3dadventure.ui.world3d

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf // Added
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.key
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahmodhota.worldfood3dadventure.data.audio.GlobalSystemManager
import com.mahmodhota.worldfood3dadventure.data.audio.SfxType
import com.mahmodhota.worldfood3dadventure.game.progress.OnboardingState
import com.mahmodhota.worldfood3dadventure.game.progress.ProgressionManager
import com.mahmodhota.worldfood3dadventure.game.world.LevelRegistry
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryProgressionChain
import androidx.compose.ui.draw.clip
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.ui.match3.components.BottomNavigationBar
import com.mahmodhota.worldfood3dadventure.ui.match3.components.FoodIcon
import com.mahmodhota.worldfood3dadventure.ui.match3.components.OnboardingTooltip
import com.mahmodhota.worldfood3dadventure.ui.match3.components.PremiumColors
import com.mahmodhota.worldfood3dadventure.ui.match3.components.TopStatusBar
import com.mahmodhota.worldfood3dadventure.ui.world3d.filament.FilamentGlobeEngine // Added
import androidx.compose.ui.text.font.FontStyle
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import com.mahmodhota.worldfood3dadventure.ui.world3d.filament.FilamentGlobeView
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

// ──────────────────────────────────────────────────────────────────────────────
// Renderer Configuration
// ──────────────────────────────────────────────────────────────────────────────

private val TWO_PI = (PI * 2).toFloat()

internal fun canStartManualMarkerFlight(isFlightActive: Boolean, activeFlightDestinationId: String?): Boolean {
    return !isFlightActive && activeFlightDestinationId == null
}

internal data class GlobeCountry(
    val id: String,
    val displayName: String,
    val flag: String,
    val isoCode: String,
    val latDeg: Float,
    val lonDeg: Float,
    val xyz: FloatArray = GlobeMath.latLonToXyz(latDeg, lonDeg)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GlobeCountry) return false
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

internal val GLOBE_COUNTRIES = listOf(
    // ── Group 1: Core Campaign (7) ──────────────────────────────────────────
    GlobeCountry("germany", "Germany", "🇩🇪", "DE", 51.165f, 10.451f),
    GlobeCountry("italy",   "Italy",   "🇮🇹", "IT", 41.871f, 12.567f),
    GlobeCountry("france",  "France",  "🇫🇷", "FR", 46.227f, 2.213f),
    GlobeCountry("spain",   "Spain",   "🇪🇸", "ES", 40.463f, -3.749f),
    GlobeCountry("sudan",   "Sudan",   "🇸🇩", "SD", 15.588f, 32.534f), // Khartoum reference
    GlobeCountry("japan",   "Japan",   "🇯🇵", "JP", 36.204f, 138.252f),
    GlobeCountry("mexico",  "Mexico",  "🇲🇽", "MX", 23.634f, -102.552f),

    // ── Group 2: Existing Global (48) ──────────────────────────────────────
    GlobeCountry("usa", "USA", "🇺🇸", "US", 37.090f, -95.712f),
    GlobeCountry("canada", "Canada", "🇨🇦", "CA", 56.130f, -106.346f),
    GlobeCountry("brazil", "Brazil", "🇧🇷", "BR", -14.235f, -51.925f),
    GlobeCountry("argentina", "Argentina", "🇦🇷", "AR", -38.416f, -63.616f),
    GlobeCountry("peru", "Peru", "🇵🇪", "PE", -9.189f, -75.015f),
    GlobeCountry("uk", "UK", "🇬🇧", "GB", 55.378f, -3.435f),
    GlobeCountry("greece", "Greece", "🇬🇷", "GR", 39.074f, 21.824f),
    GlobeCountry("sweden", "Sweden", "🇸🇪", "SE", 60.128f, 18.643f),
    GlobeCountry("portugal", "Portugal", "🇵🇹", "PT", 39.399f, -8.224f),
    GlobeCountry("switzerland", "Switzerland", "🇨🇭", "CH", 46.818f, 8.227f),
    GlobeCountry("russia", "Russia", "🇷🇺", "RU", 61.524f, 105.318f),
    GlobeCountry("china", "China", "🇨🇳", "CN", 35.861f, 104.195f),
    GlobeCountry("korea", "Korea", "🇰🇷", "KR", 35.907f, 127.766f),
    GlobeCountry("thailand", "Thailand", "🇹🇭", "TH", 15.870f, 100.992f),
    GlobeCountry("vietnam", "Vietnam", "🇻🇳", "VN", 14.058f, 108.277f),
    GlobeCountry("india", "India", "🇮🇳", "IN", 20.593f, 78.962f),
    GlobeCountry("indonesia", "Indonesia", "🇮🇩", "ID", -0.789f, 113.921f),
    GlobeCountry("philippines", "Philippines", "🇵🇭", "PH", 12.879f, 121.774f),
    GlobeCountry("turkey", "Turkey", "🇹🇷", "TR", 38.963f, 35.243f),
    GlobeCountry("egypt", "Egypt", "🇪🇬", "EG", 26.820f, 30.802f),
    GlobeCountry("morocco", "Morocco", "🇲🇦", "MA", 31.791f, -7.092f),
    GlobeCountry("saudi", "Saudi Arabia", "🇸🇦", "SA", 23.885f, 45.079f),
    GlobeCountry("israel", "Israel", "🇮🇱", "IL", 31.046f, 34.851f),
    GlobeCountry("nigeria", "Nigeria", "🇳🇬", "NG", 9.082f, 8.675f),
    GlobeCountry("south_africa", "South Africa", "🇿🇦", "ZA", -30.559f, 22.937f),
    GlobeCountry("ethiopia", "Ethiopia", "🇪🇹", "ET", 9.145f, 40.489f),
    GlobeCountry("kenya", "Kenya", "🇰🇪", "KE", -0.023f, 37.906f),
    GlobeCountry("australia", "Australia", "🇦🇺", "AU", -25.274f, 133.775f),
    GlobeCountry("netherlands", "Netherlands", "🇳🇱", "NL", 52.132f, 5.291f),
    GlobeCountry("belgium", "Belgium", "🇧🇪", "BE", 50.503f, 4.469f),
    GlobeCountry("austria", "Austria", "🇦🇹", "AT", 47.516f, 14.550f),
    GlobeCountry("norway", "Norway", "🇳🇴", "NO", 60.472f, 8.468f),
    GlobeCountry("denmark", "Denmark", "🇩🇰", "DK", 56.263f, 9.501f),
    GlobeCountry("finland", "Finland", "🇫🇮", "FI", 61.924f, 25.748f),
    GlobeCountry("poland", "Poland", "🇵🇱", "PL", 51.919f, 19.145f),
    GlobeCountry("ukraine", "Ukraine", "🇺🇦", "UA", 48.379f, 31.165f),
    GlobeCountry("ireland", "Ireland", "🇮🇪", "IE", 53.412f, -8.243f),
    GlobeCountry("colombia", "Colombia", "🇨🇴", "CO", 4.570f, -74.297f),
    GlobeCountry("chile", "Chile", "🇨🇱", "CL", -35.675f, -71.542f),
    GlobeCountry("malaysia", "Malaysia", "🇲🇾", "MY", 4.210f, 101.975f),
    GlobeCountry("singapore", "Singapore", "🇸🇬", "SG", 1.352f, 103.819f),
    GlobeCountry("new_zealand", "New Zealand", "🇳🇿", "NZ", -40.900f, 174.885f),
    GlobeCountry("pakistan", "Pakistan", "🇵🇰", "PK", 30.375f, 69.345f),
    GlobeCountry("iran", "Iran", "🇮🇷", "IR", 32.427f, 53.688f),
    GlobeCountry("iraq", "Iraq", "🇮🇶", "IQ", 33.223f, 43.679f),
    GlobeCountry("algeria", "Algeria", "🇩🇿", "DZ", 28.033f, 1.659f),
    GlobeCountry("tanzania", "Tanzania", "🇹🇿", "TZ", -6.369f, 34.888f),
    GlobeCountry("venezuela", "Venezuela", "🇻🇪", "VE", 6.423f, -66.589f),

    // ── Group 3: P7 Expansion (50) ──────────────────────────────────────────
    GlobeCountry("iceland", "Iceland", "🇮🇸", "IS", 64.963f, -19.020f),
    GlobeCountry("hungary", "Hungary", "🇭🇺", "HU", 47.162f, 19.503f),
    GlobeCountry("czech_republic", "Czech Republic", "🇨🇿", "CZ", 49.817f, 15.473f),
    GlobeCountry("romania", "Romania", "🇷🇴", "RO", 45.943f, 24.966f),
    GlobeCountry("bulgaria", "Bulgaria", "🇧🇬", "BG", 42.733f, 25.485f),
    GlobeCountry("croatia", "Croatia", "🇭🇷", "HR", 45.100f, 15.200f),
    GlobeCountry("serbia", "Serbia", "🇷🇸", "RS", 44.016f, 21.005f),
    GlobeCountry("slovakia", "Slovakia", "🇸🇰", "SK", 48.669f, 19.699f),
    GlobeCountry("lithuania", "Lithuania", "🇱🇹", "LT", 55.169f, 23.881f),
    GlobeCountry("latvia", "Latvia", "🇱🇻", "LV", 56.879f, 24.603f),
    GlobeCountry("kazakhstan", "Kazakhstan", "🇰🇿", "KZ", 48.019f, 66.923f),
    GlobeCountry("uzbekistan", "Uzbekistan", "🇺🇿", "UZ", 41.377f, 64.585f),
    GlobeCountry("mongolia", "Mongolia", "🇲🇳", "MN", 46.862f, 103.846f),
    GlobeCountry("nepal", "Nepal", "🇳🇵", "NP", 28.394f, 84.124f),
    GlobeCountry("bangladesh", "Bangladesh", "🇧🇩", "BD", 23.685f, 90.356f),
    GlobeCountry("sri_lanka", "Sri Lanka", "🇱🇰", "LK", 7.873f, 80.771f),
    GlobeCountry("cambodia", "Cambodia", "🇰🇭", "KH", 12.565f, 104.991f),
    GlobeCountry("laos", "Laos", "🇱🇦", "LA", 19.856f, 102.495f),
    GlobeCountry("myanmar", "Myanmar", "🇲🇲", "MM", 21.916f, 95.956f),
    GlobeCountry("jordan", "Jordan", "🇯🇴", "JO", 31.245f, 36.511f),
    GlobeCountry("ghana", "Ghana", "🇬🇭", "GH", 7.946f, -1.023f),
    GlobeCountry("ivory_coast", "Ivory Coast", "🇨🇮", "CI", 7.539f, -5.547f),
    GlobeCountry("senegal", "Senegal", "🇸🇳", "SN", 14.497f, -14.452f),
    GlobeCountry("uganda", "Uganda", "🇺🇬", "UG", 1.373f, 32.290f),
    GlobeCountry("rwanda", "Rwanda", "🇷🇼", "RW", -1.940f, 29.873f),
    GlobeCountry("zambia", "Zambia", "🇿🇲", "ZM", -13.133f, 27.849f),
    GlobeCountry("zimbabwe", "Zimbabwe", "🇿🇼", "ZW", -19.015f, 29.154f),
    GlobeCountry("madagascar", "Madagascar", "🇲🇬", "MG", -18.766f, 46.869f),
    GlobeCountry("tunisia", "Tunisia", "🇹🇳", "TN", 33.886f, 9.537f),
    GlobeCountry("libya", "Libya", "🇱🇾", "LY", 26.335f, 17.228f),
    GlobeCountry("costa_rica", "Costa Rica", "🇨🇷", "CR", 9.748f, -83.753f),
    GlobeCountry("panama", "Panama", "🇵🇦", "PA", 8.537f, -80.782f),
    GlobeCountry("guatemala", "Guatemala", "🇬🇹", "GT", 15.783f, -90.230f),
    GlobeCountry("cuba", "Cuba", "🇨🇺", "CU", 21.521f, -77.781f),
    GlobeCountry("jamaica", "Jamaica", "🇯🇲", "JM", 18.109f, -77.297f),
    GlobeCountry("dominican_republic", "Dominican Republic", "🇩🇴", "DO", 18.735f, -70.162f),
    GlobeCountry("haiti", "Haiti", "🇭🇹", "HT", 18.971f, -72.694f),
    GlobeCountry("bahamas", "Bahamas", "🇧🇸", "BS", 25.034f, -77.396f),
    GlobeCountry("greenland", "Greenland", "🇬🇱", "GL", 71.706f, -42.604f),
    GlobeCountry("puerto_rico", "Puerto Rico", "🇵🇷", "PR", 18.220f, -66.590f),
    GlobeCountry("ecuador", "Ecuador", "🇪🇨", "EC", -1.831f, -78.183f),
    GlobeCountry("bolivia", "Bolivia", "🇧🇴", "BO", -16.290f, -63.588f),
    GlobeCountry("paraguay", "Paraguay", "🇵🇾", "PY", -23.442f, -58.443f),
    GlobeCountry("uruguay", "Uruguay", "🇺🇾", "UY", -32.522f, -55.765f),
    GlobeCountry("guyana", "Guyana", "🇬🇾", "GY", 4.860f, -58.930f),
    GlobeCountry("fiji", "Fiji", "🇫🇯", "FJ", -17.713f, 178.065f),
    GlobeCountry("papua_new_guinea", "Papua New Guinea", "🇵🇬", "PG", -6.314f, 143.955f),
    GlobeCountry("samoa", "Samoa", "🇼🇸", "WS", -13.759f, -172.104f),
    GlobeCountry("tonga", "Tonga", "🇹🇴", "TO", -21.178f, -175.198f),
    GlobeCountry("vanuatu", "Vanuatu", "🇻🇺", "VU", -15.376f, 166.959f),

    // ── Group 4: P10 Expansion (108) ──────────────────────────────────────────
    GlobeCountry("albania", "Albania", "🇦🇱", "AL", 41.153f, 20.168f),
    GlobeCountry("andorra", "Andorra", "🇦🇩", "AD", 42.506f, 1.522f),
    GlobeCountry("armenia", "Armenia", "🇦🇲", "AM", 40.069f, 45.038f),
    GlobeCountry("azerbaijan", "Azerbaijan", "🇦🇿", "AZ", 40.143f, 47.577f),
    GlobeCountry("belarus", "Belarus", "🇧🇾", "BY", 53.710f, 27.953f),
    GlobeCountry("bosnia", "Bosnia", "🇧🇦", "BA", 43.916f, 17.679f),
    GlobeCountry("cyprus", "Cyprus", "🇨🇾", "CY", 35.126f, 33.430f),
    GlobeCountry("estonia", "Estonia", "🇪🇪", "EE", 58.595f, 25.014f),
    GlobeCountry("georgia", "Georgia", "🇬🇪", "GE", 42.315f, 43.357f),
    GlobeCountry("kosovo", "Kosovo", "🇽🇰", "XK", 42.603f, 20.903f),
    GlobeCountry("liechtenstein", "Liechtenstein", "🇱🇮", "LI", 47.166f, 9.555f),
    GlobeCountry("luxembourg", "Luxembourg", "🇱🇺", "LU", 49.815f, 6.130f),
    GlobeCountry("malta", "Malta", "🇲🇹", "MT", 35.938f, 14.375f),
    GlobeCountry("moldova", "Moldova", "🇲🇩", "MD", 47.412f, 28.370f),
    GlobeCountry("monaco", "Monaco", "🇲🇨", "MC", 43.738f, 7.425f),
    GlobeCountry("montenegro", "Montenegro", "🇲🇪", "ME", 42.709f, 19.374f),
    GlobeCountry("north_macedonia", "North Macedonia", "🇲🇰", "MK", 41.609f, 21.745f),
    GlobeCountry("san_marino", "San Marino", "🇸🇲", "SM", 43.942f, 12.458f),
    GlobeCountry("slovenia", "Slovenia", "🇸🇮", "SI", 46.151f, 14.996f),
    GlobeCountry("vatican_city", "Vatican City", "🇻🇦", "VA", 41.903f, 12.453f),
    GlobeCountry("afghanistan", "Afghanistan", "🇦🇫", "AF", 33.939f, 67.710f),
    GlobeCountry("bahrain", "Bahrain", "🇧🇭", "BH", 26.067f, 50.558f),
    GlobeCountry("bhutan", "Bhutan", "🇧🇹", "BT", 27.514f, 90.434f),
    GlobeCountry("brunei", "Brunei", "🇧🇳", "BN", 4.535f, 114.728f),
    GlobeCountry("east_timor", "East Timor", "🇹🇱", "TL", -8.874f, 125.728f),
    GlobeCountry("kuwait", "Kuwait", "🇰🇼", "KW", 29.312f, 47.482f),
    GlobeCountry("kyrgyzstan", "Kyrgyzstan", "🇰🇬", "KG", 41.204f, 74.766f),
    GlobeCountry("lebanon", "Lebanon", "🇱🇧", "LB", 33.855f, 35.862f),
    GlobeCountry("maldives", "Maldives", "🇲🇻", "MV", 3.203f, 73.221f),
    GlobeCountry("north_korea", "North Korea", "🇰🇵", "KP", 40.340f, 127.510f),
    GlobeCountry("oman", "Oman", "🇴🇲", "OM", 21.474f, 55.975f),
    GlobeCountry("palestine", "Palestine", "🇵🇸", "PS", 31.952f, 35.233f),
    GlobeCountry("qatar", "Qatar", "🇶🇦", "QA", 25.355f, 51.184f),
    GlobeCountry("syria", "Syria", "🇸🇾", "SY", 34.802f, 38.997f),
    GlobeCountry("taiwan", "Taiwan", "🇹🇼", "TW", 23.698f, 120.961f),
    GlobeCountry("tajikistan", "Tajikistan", "🇹🇯", "TJ", 38.861f, 71.276f),
    GlobeCountry("turkmenistan", "Turkmenistan", "🇹🇲", "TM", 38.970f, 59.556f),
    GlobeCountry("uae", "UAE", "🇦🇪", "AE", 23.424f, 53.848f),
    GlobeCountry("yemen", "Yemen", "🇾🇪", "YE", 15.553f, 48.516f),
    GlobeCountry("macau", "Macau", "🇲🇴", "MO", 22.199f, 113.544f),
    GlobeCountry("angola", "Angola", "🇦🇴", "AO", -11.203f, 17.874f),
    GlobeCountry("benin", "Benin", "🇧🇯", "BJ", 9.308f, 2.316f),
    GlobeCountry("botswana", "Botswana", "🇧🇼", "BW", -22.329f, 24.685f),
    GlobeCountry("burkina_faso", "Burkina Faso", "🇧🇫", "BF", 12.238f, -1.562f),
    GlobeCountry("burundi", "Burundi", "🇧🇮", "BI", -3.373f, 29.919f),
    GlobeCountry("cabo_verde", "Cabo Verde", "🇨🇻", "CV", 16.002f, -24.013f),
    GlobeCountry("cameroon", "Cameroon", "🇨🇲", "CM", 7.370f, 12.355f),
    GlobeCountry("central_african_republic", "CAR", "🇨🇫", "CF", 6.611f, 20.939f),
    GlobeCountry("chad", "Chad", "🇹🇩", "TD", 15.454f, 18.732f),
    GlobeCountry("comoros", "Comoros", "🇰🇲", "KM", -11.646f, 43.333f),
    GlobeCountry("drc", "DRC", "🇨🇩", "CD", -4.038f, 21.759f),
    GlobeCountry("republic_congo", "Congo", "🇨🇬", "CG", -0.228f, 15.828f),
    GlobeCountry("djibouti", "Djibouti", "🇩🇯", "DJ", 11.825f, 42.590f),
    GlobeCountry("equatorial_guinea", "Equatorial Guinea", "🇬🇶", "GQ", 1.651f, 10.268f),
    GlobeCountry("eritrea", "Eritrea", "🇪🇷", "ER", 15.179f, 39.782f),
    GlobeCountry("eswatini", "Eswatini", "🇸🇿", "SZ", -26.523f, 31.466f),
    GlobeCountry("gabon", "Gabon", "🇬🇦", "GA", -0.804f, 11.609f),
    GlobeCountry("gambia", "Gambia", "🇬🇲", "GM", 13.443f, -15.310f),
    GlobeCountry("guinea", "Guinea", "🇬🇳", "GN", 9.946f, -9.697f),
    GlobeCountry("guinea_bissau", "Guinea-Bissau", "🇬🇼", "GW", 11.804f, -15.180f),
    GlobeCountry("lesotho", "Lesotho", "🇱🇸", "LS", -29.610f, 28.234f),
    GlobeCountry("liberia", "Liberia", "🇱🇷", "LR", 6.428f, -9.430f),
    GlobeCountry("malawi", "Malawi", "🇲🇼", "MW", -13.254f, 34.302f),
    GlobeCountry("mali", "Mali", "🇲🇱", "ML", 17.571f, -4.000f),
    GlobeCountry("mauritania", "Mauritania", "🇲🇷", "MR", 21.008f, -10.941f),
    GlobeCountry("mauritius", "Mauritius", "🇲🇺", "MU", -20.348f, 57.552f),
    GlobeCountry("namibia", "Namibia", "🇳🇦", "NA", -22.958f, 18.490f),
    GlobeCountry("niger", "Niger", "🇳🇪", "NE", 17.608f, 8.082f),
    GlobeCountry("sao_tome", "Sao Tome", "🇸🇹", "ST", 0.186f, 6.613f),
    GlobeCountry("seychelles", "Seychelles", "🇸🇨", "SC", -4.680f, 55.492f),
    GlobeCountry("kiribati", "Kiribati", "🇰🇮", "KI", -3.370f, -168.734f),
    GlobeCountry("marshall_islands", "Marshall Islands", "🇲🇭", "MH", 7.132f, 171.185f),
    GlobeCountry("micronesia", "Micronesia", "🇫🇲", "FM", 7.426f, 150.551f),
    GlobeCountry("nauru", "Nauru", "🇳🇷", "NR", -0.523f, 166.932f),
    GlobeCountry("palau", "Palau", "🇵🇼", "PW", 7.515f, 134.583f),
    GlobeCountry("solomon_islands", "Solomon Islands", "🇸🇧", "SB", -9.646f, 160.156f),
    GlobeCountry("tuvalu", "Tuvalu", "🇹🇻", "TV", -7.110f, 177.649f),
    GlobeCountry("cook_islands", "Cook Islands", "🇨🇰", "CK", -21.237f, -159.778f),
    GlobeCountry("niue", "Niue", "🇳🇺", "NU", -19.054f, -169.867f),
    GlobeCountry("french_polynesia", "French Polynesia", "🇵🇫", "PF", -17.680f, -149.407f),
    GlobeCountry("antigua_barbuda", "Antigua & Barbuda", "🇦🇬", "AG", 17.061f, -61.796f),
    GlobeCountry("barbados", "Barbados", "🇧🇧", "BB", 13.194f, -59.543f),
    GlobeCountry("belize", "Belize", "🇧🇿", "BZ", 17.190f, -88.498f),
    GlobeCountry("dominica", "Dominica", "🇩🇲", "DM", 15.415f, -61.371f),
    GlobeCountry("el_salvador", "El Salvador", "🇸🇻", "SV", 13.794f, -88.897f),
    GlobeCountry("grenada", "Grenada", "🇬🇩", "GD", 12.117f, -61.679f),
    GlobeCountry("honduras", "Honduras", "🇭🇳", "HN", 15.200f, -86.242f),
    GlobeCountry("nicaragua", "Nicaragua", "🇳🇮", "NI", 12.865f, -85.207f),
    GlobeCountry("st_kitts_nevis", "St. Kitts & Nevis", "🇰🇳", "KN", 17.358f, -62.783f),
    GlobeCountry("st_lucia", "St. Lucia", "🇱🇨", "LC", 13.909f, -60.979f),
    GlobeCountry("st_vincent", "St. Vincent", "🇻🇨", "VC", 12.984f, -61.287f),
    GlobeCountry("trinidad_tobago", "Trinidad & Tobago", "🇹🇹", "TT", 10.692f, -61.223f),
    GlobeCountry("bermuda", "Bermuda", "🇧🇲", "BM", 32.308f, -64.751f),
    GlobeCountry("cayman_islands", "Cayman Islands", "🇰🇾", "KY", 19.313f, -81.255f),
    GlobeCountry("bvi", "BVI", "🇻🇬", "VG", 18.421f, -64.640f),
    GlobeCountry("suriname", "Suriname", "🇸🇷", "SR", 3.919f, -56.028f),
    GlobeCountry("falkland_islands", "Falkland Islands", "🇫🇰", "FK", -51.796f, -59.524f),
    GlobeCountry("french_guiana", "French Guiana", "🇬🇫", "GF", 3.934f, -53.126f),
    GlobeCountry("sierra_leone", "Sierra Leone", "🇸🇱", "SL", 8.461f, -11.780f),
    GlobeCountry("somalia", "Somalia", "🇸🇴", "SO", 5.152f, 46.200f),
    GlobeCountry("south_sudan", "South Sudan", "🇸🇸", "SS", 6.877f, 31.307f),
    GlobeCountry("togo", "Togo", "🇹🇬", "TG", 8.620f, 0.825f),
    GlobeCountry("reunion", "Reunion", "🇷🇪", "RE", -21.115f, 55.536f),
    GlobeCountry("mayotte", "Mayotte", "🇾🇹", "YT", -12.828f, 45.166f),
    GlobeCountry("st_helena", "St. Helena", "🇸🇭", "SH", -15.965f, -5.709f),
    GlobeCountry("western_sahara", "Western Sahara", "🇪🇭", "EH", 24.216f, -12.886f),
    GlobeCountry("montserrat", "Montserrat", "🇲🇸", "MS", 16.743f, -62.187f),
    GlobeCountry("turks_caicos", "Turks & Caicos", "🇹🇨", "TC", 21.694f, -71.798f)
)

private val GLOBE_COUNTRIES_BY_ID = GLOBE_COUNTRIES.associateBy { it.id }

private val COUNTRY_ACCENT_GRADIENT: Map<String, Pair<Color, Color>> = mapOf(
    "germany" to Pair(Color(0xFF1A3B1E), Color(0xFF0D1F12)),
    "italy"   to Pair(Color(0xFF2D3010), Color(0xFF161808)),
    "france"  to Pair(Color(0xFF0D1845), Color(0xFF060E28)),
    "spain"   to Pair(Color(0xFF461208), Color(0xFF1E0804)),
    "japan"   to Pair(Color(0xFF170F3D), Color(0xFF0A0820)),
    "mexico"  to Pair(Color(0xFF0C2E3C), Color(0xFF061820)),
    "sudan"   to Pair(Color(0xFF2C1404), Color(0xFF160A02))
)

private const val IDLE_ROT_SPEED       = 0.055f
private const val IDLE_RESUME_DELAY_MS = 2200f

internal enum class MarkerState {
    LOCKED,
    UNLOCKED,
    CURRENT,
    COMPLETED
}

internal fun resolveMarkerState(
    countryId: String,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isNextDestination: Boolean
): MarkerState {
    return when {
        isNextDestination -> MarkerState.CURRENT
        isCompleted -> MarkerState.COMPLETED
        isUnlocked -> MarkerState.UNLOCKED
        else -> MarkerState.LOCKED
    }
}

@Composable
private fun CountryMarker(
    country: GlobeCountry,
    engine: FilamentGlobeEngine,
    camera: GlobeCameraState, // Added
    isSelected: Boolean,
    isDimmed: Boolean,
    state: MarkerState,
    isUnlockHighlighted: Boolean,
    totalStars: Int,
    completedLevels: Int,
    totalLevels: Int,
    arrivalBoost: Float,
    pulsePhase: Float,
    unlockPulse: Float,
    onClick: () -> Unit
) {
    val showLabel = isSelected || state == MarkerState.CURRENT

    val pinScale by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.30f
            state == MarkerState.CURRENT -> 1.15f
            isDimmed -> 0.85f
            else -> 1.0f + arrivalBoost * 0.3f
        },
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pinScale"
    )
    
    val markerAlpha by animateFloatAsState(
        targetValue = when {
            isSelected -> 1.0f
            state == MarkerState.CURRENT -> 1.0f
            isDimmed -> 0.40f
            else -> 1.0f
        },
        label = "markerAlpha"
    )

    val pulseScale = if (state == MarkerState.CURRENT && !isSelected) {
        1f + 0.10f * (0.5f + 0.5f * sin(pulsePhase.toDouble() * 1.5).toFloat())
    } else 1f

    Box(
        modifier = Modifier
            .offset {
                // P10-AO: REAL-TIME POSITION PULL WITH INVALIDATION
                // We read camera.rotation to ensure this lambda is re-run every frame 
                // during animation, ensuring markers perfectly track the globe pixels.
                val rx = camera.rotationX
                val ry = camera.rotationY
                
                val proj = engine.project(country.xyz) ?: return@offset IntOffset.Zero
                val pos = proj.offset
                
                val circleSize = if (isSelected) 24.dp.toPx() else 16.dp.toPx()
                val triangleHeight = 5.dp.toPx()
                
                androidx.compose.ui.unit.IntOffset(
                    x = (pos.x - 24.dp.toPx()).toInt(),
                    y = (pos.y - circleSize - triangleHeight - (arrivalBoost * 15f).dp.toPx()).toInt()
                )
            }
            .size(48.dp, 80.dp)
            .graphicsLayer {
                // Ensure visibility is also updated every frame
                val rx = camera.rotationX
                val ry = camera.rotationY
                
                val proj = engine.project(country.xyz) ?: return@graphicsLayer
                val visibility = proj.visibility
                
                scaleX = pinScale * pulseScale
                scaleY = pinScale * pulseScale
                
                val horizonFade = if (visibility < 0.15f) (visibility / 0.15f) else 1.0f
                alpha = markerAlpha * horizonFade
            }
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = {
                    GlobalSystemManager.audio.playSfx(SfxType.COUNTRY_MARKER_SELECT)
                    onClick()
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Selection/Current Glow - Smaller and softer
        if (isSelected || state == MarkerState.CURRENT || arrivalBoost > 0.01f) {
            val glowColor = if (state == MarkerState.COMPLETED) Color(0xFFFACC15) else PremiumColors.Gold
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(glowColor.copy(alpha = (arrivalBoost.coerceAtLeast(0.3f)) * 0.4f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1. The Main Marker Body (Significantly Smaller)
            Box(
                modifier = Modifier
                    .size(if (isSelected) 24.dp else 16.dp)
                    .shadow(if (isSelected) 6.dp else 1.5.dp, CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = when(state) {
                                MarkerState.CURRENT -> listOf(Color(0xFFFFF176), Color(0xFFFFD54F))
                                MarkerState.COMPLETED -> listOf(Color(0xFFFFE082), Color(0xFFF9A825))
                                MarkerState.UNLOCKED -> listOf(Color(0xFFF5F5F5), Color(0xFFBDBDBD))
                                MarkerState.LOCKED -> listOf(Color(0xFF757575), Color(0xFF424242))
                            }
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = if (isSelected) 1.2.dp else 0.8.dp, 
                        color = when {
                            isSelected -> Color(0xFFFFD700)
                            state == MarkerState.CURRENT -> Color(0xFFFFEA00).copy(alpha = 0.7f)
                            state == MarkerState.COMPLETED -> Color(0xFFFFB300).copy(alpha = 0.5f)
                            else -> Color.White.copy(alpha = 0.2f)
                        }, 
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Show ISO code only for CURRENT or SELECTED if large enough
                if (showLabel && state != MarkerState.LOCKED) {
                    Text(
                        text = country.isoCode,
                        color = Color(0xFF1A1A1A),
                        fontSize = if (isSelected) 9.sp else 7.sp,
                        fontWeight = FontWeight.Black
                    )
                } else if (state == MarkerState.LOCKED) {
                    Text(
                        "🔒", 
                        fontSize = 8.sp,
                        modifier = Modifier.graphicsLayer { alpha = 0.6f }
                    )
                }
                
                // Completed Checkmark Mini (always visible if completed)
                if (state == MarkerState.COMPLETED) {
                    Text(
                        "✓", 
                        color = Color(0xFF064E3B), 
                        fontSize = if (isSelected) 8.sp else 6.sp, 
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.BottomCenter).offset(y = 2.dp)
                    )
                }
            }
            
            // 2. The Triangle Tip (Smaller)
            Canvas(modifier = Modifier.size(6.dp, 5.dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                }
                drawPath(
                    path = path, 
                    color = when(state) {
                        MarkerState.CURRENT -> Color(0xFFFFD54F)
                        MarkerState.COMPLETED -> Color(0xFFF9A825)
                        else -> Color(0xFFBDBDBD)
                    }
                )
            }
            
            // 3. Info below tip (Stars) - only for Unlocked/Completed
            if (state != MarkerState.LOCKED && totalStars > 0 && !isDimmed) {
                Text(
                    text = "★$totalStars", 
                    color = if (state == MarkerState.COMPLETED) Color(0xFFFFD700) else Color.White, 
                    fontSize = 8.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }

        if (isUnlockHighlighted) {
            val flagWaveY = (4.dp * sin(pulsePhase.toDouble() * 2.0).toFloat())
            Text(text = country.flag, fontSize = 20.sp, modifier = Modifier.align(Alignment.TopCenter).offset(y = (-36).dp + flagWaveY).graphicsLayer { alpha = 0.6f + 0.4f * unlockPulse })
        }
        
        if (state == MarkerState.CURRENT && !isSelected) {
            Text(
                "NEXT",
                color = PremiumColors.Gold,
                fontSize = 7.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-12).dp),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun Globe3DScreen(
    onLevelSelected: (String, Int) -> Unit,
    onShowLevels: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val gameState by GameProgressManager.repository.state.collectAsState()
    val hasSeenOnboarding = gameState.hasSeenOnboarding
    val onboardingStep = gameState.onboardingState
    val camera = remember { GlobeCameraState() }
    var engineInstance by remember { mutableStateOf<com.mahmodhota.worldfood3dadventure.ui.world3d.filament.FilamentGlobeEngine?>(null) }
    var sunPhase by remember { mutableFloatStateOf(0f) }
    var pulsePhase by remember { mutableFloatStateOf(0f) }
    var selectedCountry by remember { mutableStateOf<GlobeCountry?>(null) }
    var currentCountryId by remember { mutableStateOf("germany") }
    var unlockBanner by remember { mutableStateOf<String?>(null) }
    var unlockHighlightCountryId by remember { mutableStateOf<String?>(null) }
    var activeFlightDestinationId by remember { mutableStateOf<String?>(null) }
    var selectedContinent by remember { mutableStateOf<com.mahmodhota.worldfood3dadventure.game.world.model.Continent?>(null) }
    
    // P10-AP: Country Search State
    var showSearch by remember { mutableStateOf(false) }

    if (showSearch) {
        androidx.activity.compose.BackHandler {
            showSearch = false
        }
    }

    val isDraggingRef = remember { booleanArrayOf(false) }
    val idleCountdownRef = remember { floatArrayOf(IDLE_RESUME_DELAY_MS) }
    val flightAnimator = remember { FlightAnimator() }
    val progressMap = ProgressionManager.progressMap
    val density = LocalDensity.current
    var bottomNavHeightPx by remember { mutableFloatStateOf(0f) }
    val navInsetBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomOverlayPadding = (with(density) { bottomNavHeightPx.toDp() } + 8.dp).coerceAtLeast(navInsetBottom + 80.dp)
    val unlockPulseTransition = rememberInfiniteTransition(label = "unlockPulse")
    val unlockPulse by unlockPulseTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 860, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "unlockPulseValue")

    LaunchedEffect(ProgressionManager.newlyUnlockedCountry) {
        val unlockedCountry = ProgressionManager.newlyUnlockedCountry
        if (unlockedCountry != null) {
            val unlockedId = unlockedCountry.countryId
            val country = GLOBE_COUNTRIES_BY_ID[unlockedId]
            if (country != null) {
                unlockHighlightCountryId = unlockedId
                unlockBanner = unlockedId
                GlobalSystemManager.audio.playSfx(SfxType.COUNTRY_UNLOCK)
                GlobalSystemManager.haptics.heavy()
                val from = GLOBE_COUNTRIES_BY_ID[currentCountryId]
                if (from != null && from.id != unlockedId && !flightAnimator.isActive) {
                    selectedCountry = null
                    camera.cancelFlyTo()
                    camera.stopInertia()
                    activeFlightDestinationId = unlockedId
                    flightAnimator.startFlight(from, country)
                }
                delay(4000)
                unlockBanner = null
                ProgressionManager.consumeUnlockEvent()
                delay(500)
                if (unlockHighlightCountryId == unlockedId) {
                    unlockHighlightCountryId = null
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val travelDestId = ProgressionManager.consumeTravelDestination()
        if (travelDestId != null) {
            val country = GLOBE_COUNTRIES_BY_ID[travelDestId]
            val from = GLOBE_COUNTRIES_BY_ID[currentCountryId]
            if (country != null && from != null && from.id != travelDestId) {
                selectedCountry = null
                camera.cancelFlyTo()
                camera.stopInertia()
                activeFlightDestinationId = travelDestId
                flightAnimator.startFlight(from, country)
            } else if (country != null) {
                selectedCountry = country
                currentCountryId = country.id
                camera.focusOn(country.latDeg, country.lonDeg)
            }
        }
    }

    LaunchedEffect(Unit) {
        var lastFrameNanos = 0L
        var sunAccumulatorMs = 0f
        while (isActive) {
            val frameTimeNanos = withFrameNanos { it }
            if (lastFrameNanos == 0L) {
                lastFrameNanos = frameTimeNanos
                continue
            }
            val rawFrameMs = ((frameTimeNanos - lastFrameNanos) / 1_000_000f).coerceIn(8f, 64f)
            lastFrameNanos = frameTimeNanos

            val interacting = isDraggingRef[0] || camera.isFlyingTo || flightAnimator.isActive
            if (interacting || camera.hasVelocity) {
                idleCountdownRef[0] = IDLE_RESUME_DELAY_MS
            } else if (idleCountdownRef[0] > 0f) {
                idleCountdownRef[0] = (idleCountdownRef[0] - rawFrameMs).coerceAtLeast(0f)
            }

            camera.tickInertia()
            camera.tickFlyTo()
            
            val step = rawFrameMs / 16f
            if (!interacting && !camera.hasVelocity) {
                // P9-A: Auto-level to upright position (rotationX -> 0) always ends user tilt
                // but we only do it if no specific flight is active.
                camera.tickAutoLevel(step)
                
                // P9-A: Constant slow auto-rotation around Y axis only when idle
                if (idleCountdownRef[0] <= 0f) {
                    camera.rotationY = (camera.rotationY + IDLE_ROT_SPEED * step) % 360f
                }
            }
            
            flightAnimator.tick(rawFrameMs)

            if (flightAnimator.isActive) {
                camera.cancelFlyTo()
                val sample = flightAnimator.currentSample()
                if (sample != null) {
                    val targetY = -sample.lonDeg
                    val targetX = sample.latDeg.coerceIn(-80f, 80f)
                    val dy = GlobeCameraState.normalizeAngleDiff(targetY - camera.rotationY)
                    camera.rotationY = (camera.rotationY + dy * 0.17f) % 360f
                    camera.rotationX += (targetX - camera.rotationX) * 0.17f
                }
            } else {
                flightAnimator.consumeArrivalDestinationId()?.let { arrivedId ->
                    GLOBE_COUNTRIES_BY_ID[arrivedId]?.let { arrived ->
                        currentCountryId = arrived.id
                        selectedCountry = arrived
                    }
                }
                if (!flightAnimator.isActive) {
                    activeFlightDestinationId = null
                }
            }

            sunAccumulatorMs += rawFrameMs
            if (sunAccumulatorMs >= 120f) {
                sunPhase = (sunPhase + 0.00032f * sunAccumulatorMs) % TWO_PI
                sunAccumulatorMs = 0f
            }
            pulsePhase = (pulsePhase + 0.022f * step) % TWO_PI
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            camera.stopInertia()
            camera.cancelFlyTo()
            flightAnimator.clear()
            unlockHighlightCountryId = null
            unlockBanner = null
        }
    }

    LaunchedEffect(camera.zoom) {
        engineInstance?.setZoom(camera.zoom)
    }

    // Outer container with interaction handling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040810))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // P7: If user starts dragging or zooming, clear selection to allow auto-leveling later
                    if (pan.getDistanceSquared() > 1f || abs(zoom - 1f) > 0.001f) {
                        selectedCountry = null
                        activeFlightDestinationId = null
                    }
                    camera.cancelFlyTo()
                    camera.applyDragImpulse(pan.x, pan.y)
                    camera.applyZoom(zoom)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {
                    flightAnimator.clear()
                    camera.resetToDefault()
                    selectedCountry = null
                    activeFlightDestinationId = null
                }) { tapOffset ->
                    // P10-L: Centralized Smart Country Selection
                    engineInstance?.let { engine ->
                        var bestCountry: GlobeCountry? = null
                        var minDistance = Float.MAX_VALUE
                        var maxVisibility = -1f
                        
                        // We iterate through all countries and project them
                        GLOBE_COUNTRIES.forEach { country ->
                            val proj = engine.project(country.xyz) ?: return@forEach
                            
                            // Check distance in pixels
                            val dist = (proj.offset - tapOffset).getDistance()
                            
                            // Touch target threshold: 24dp radius = 48dp diameter
                            val threshold = with(density) { 32.dp.toPx() } 
                            
                            // Deterministic logic: 
                            // 1. Must be within threshold
                            // 2. If multiple within threshold, prefer one with higher visibility (more front-facing)
                            // 3. Or if visibilities are similar, prefer closest distance
                            
                            if (dist < threshold) {
                                // If visibility is significantly better (> 0.2 difference), prefer it
                                // Otherwise prefer the closer one
                                if (proj.visibility > maxVisibility + 0.15f || (abs(proj.visibility - maxVisibility) < 0.15f && dist < minDistance)) {
                                    minDistance = dist
                                    maxVisibility = proj.visibility
                                    bestCountry = country
                                }
                            }
                        }

                        if (bestCountry != null) {
                            val country = bestCountry!!
                            android.util.Log.d("Globe3D", "Smart selected: ${country.id}")
                            if (!canStartManualMarkerFlight(flightAnimator.isActive, activeFlightDestinationId)) return@detectTapGestures
                            
                            if (selectedCountry?.id == country.id) {
                                selectedCountry = null
                            } else {
                                val from = GLOBE_COUNTRIES_BY_ID[currentCountryId]
                                if (from != null && from.id != country.id) {
                                    selectedCountry = null
                                    camera.cancelFlyTo()
                                    camera.stopInertia()
                                    activeFlightDestinationId = country.id
                                    flightAnimator.startFlight(from, country)
                                } else {
                                    selectedCountry = country
                                    currentCountryId = country.id
                                    camera.startFlyTo(country.latDeg, country.lonDeg)
                                }
                            }
                        } else {
                            selectedCountry = null
                        }
                    }
                }
            }
            .pointerInput("dragState") {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        isDraggingRef[0] = event.changes.any { it.pressed }
                    }
                }
            }
    ) {
        // 1. Bottom Layer: Filament Globe
        FilamentGlobeView(
            onFailure = { android.util.Log.e("Globe3D", "Globe failed to load!") },
            modifier = Modifier.fillMaxSize(),
            rotationX = camera.rotationX,
            rotationY = camera.rotationY,
            onEngineReady = { 
                android.util.Log.d("Globe3D", "Engine ready. Setting zoom=${camera.zoom}")
                engineInstance = it
                it.setZoom(camera.zoom)
            }
        )
        
        // 2. Middle Layer: Markers and Flight Paths
        engineInstance?.let { engine ->
            
            val nextSpec = remember(progressMap.size) { 
                CountryProgressionChain.UNLOCK_ORDER.find { spec ->
                    val p = progressMap[spec.countryId]
                    (p?.isUnlocked == true || spec.unlockedInitially) && p?.isCompleted != true
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                // P10-AO: Optimized marker pipeline for Natural Motion
                // We quantize the rotation for sorting/culling to avoid expensive List operations every frame.
                // However, the individual markers will still PULL smooth position data in their offset lambdas.
                val rySort by remember { derivedStateOf { (camera.rotationY / 1.0f).toInt() } }
                val rxSort by remember { derivedStateOf { (camera.rotationX / 1.0f).toInt() } }

                val visibleMarkers = remember(rySort, rxSort, selectedCountry?.id, selectedContinent) {
                    val ry = rySort.toFloat()
                    val rx = rxSort.toFloat()
                    GLOBE_COUNTRIES.asSequence()
                        .filter { country ->
                            if (selectedContinent == null) true
                            else LevelRegistry.getCountry(country.id)?.metadata?.continent == selectedContinent
                        }
                        .map { country ->
                            // Use the pre-calculated XYZ to avoid allocations
                            val depth = GlobeMath.getZDepth(country.xyz, ry, rx)
                            val finalDepth = if (selectedCountry?.id == country.id) depth + 10f else depth
                            country to finalDepth
                        }
                        // Pre-cull items far behind the horizon
                        .filter { it.second > -0.25f }
                        .sortedBy { it.second }
                        .map { it.first }
                        .toList()
                }

                visibleMarkers.forEach { country ->
                    val progress = progressMap[country.id]
                    
                    val isUnlocked = progress?.isUnlocked == true || 
                        CountryProgressionChain.isInitiallyUnlocked(country.id)
                    val isCompleted = progress?.isCompleted == true
                    val isNext = nextSpec?.countryId == country.id
                    val isDimmed = selectedCountry != null && selectedCountry?.id != country.id
                    
                    key(country.id) {
                        CountryMarker(
                            country = country,
                            engine = engine,
                            camera = camera, // Added
                            isSelected = selectedCountry?.id == country.id,
                            isDimmed = isDimmed,
                            state = resolveMarkerState(country.id, isUnlocked, isCompleted, isNext),
                            isUnlockHighlighted = unlockHighlightCountryId == country.id,
                            totalStars = progress?.totalStars ?: 0,
                            completedLevels = progress?.levels?.count { it.isCompleted } ?: 0,
                            totalLevels = CountryProgressionChain.getTotalLevels(country.id),
                            arrivalBoost = flightAnimator.arrivalBoostFor(country.id),
                            pulsePhase = pulsePhase,
                            unlockPulse = unlockPulse,
                            onClick = {
                                android.util.Log.d("Globe3D", "Marker clicked: ${country.id}")
                                if (!canStartManualMarkerFlight(flightAnimator.isActive, activeFlightDestinationId)) return@CountryMarker
                                if (selectedCountry?.id == country.id) {
                                    selectedCountry = null
                                } else {
                                    val from = GLOBE_COUNTRIES_BY_ID[currentCountryId]
                                    if (from != null && from.id != country.id) {
                                        selectedCountry = null
                                        camera.cancelFlyTo()
                                        camera.stopInertia()
                                        activeFlightDestinationId = country.id
                                        flightAnimator.startFlight(from, country)
                                    } else {
                                        selectedCountry = country
                                        currentCountryId = country.id
                                        GlobalSystemManager.audio.playSfx(SfxType.COUNTRY_CARD_OPEN)
                                        camera.startFlyTo(country.latDeg, country.lonDeg)
                                    }
                                }
                            }
                        )
                    }
                }

                flightAnimator.currentSample()?.let { sample ->
                    engine.project(sample.latDeg, sample.lonDeg)?.let { result ->
                        Text(
                            text = "\u2708\ufe0f",
                            fontSize = 22.sp,
                            modifier = Modifier
                                .offset {
                                    androidx.compose.ui.unit.IntOffset(
                                        (result.offset.x - 11.dp.toPx()).toInt(),
                                        (result.offset.y - 11.dp.toPx()).toInt()
                                    )
                                }
                                .graphicsLayer { alpha = result.visibility.coerceIn(0f, 1f) }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = selectedCountry != null,
            enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut(tween(300)) + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedCountry?.let { country ->
                GlobeCountryCard(
                    country       = country,
                    onPlayLevel   = { lvl -> onLevelSelected(country.id, lvl) },
                    onShowLevels  = { onShowLevels(country.id) },
                    onDismiss     = { selectedCountry = null },
                    bottomClearance = bottomOverlayPadding
                )
            }
        }

        AnimatedVisibility(
            visible = unlockBanner != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 70.dp)
        ) {
            unlockBanner?.let { id -> CountryUnlockBanner(countryId = id) }
        }

        // Top Layer UI (Grouped to avoid separate overlay boxes)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            TopStatusBar(onSettingsClick = onSettingsClick)
            
            Spacer(Modifier.height(4.dp))
            
            // P10-J: World Journey HUD - Compact, transparent, and responsive
            WorldJourneyHUD(
                progressMap = progressMap,
                onSearchClick = { showSearch = true } // Added search integration
            )
        }

        // P10-N: Consolidated Single Continent Selector at Bottom
        AnimatedVisibility(
            visible = selectedCountry == null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomOverlayPadding + 10.dp)
        ) {
            ContinentSelector(
                selected = selectedContinent,
                onContinentSelected = { selectedContinent = if (selectedContinent == it) null else it }
            )
        }

        // Debug Layer
        if (com.mahmodhota.worldfood3dadventure.BuildConfig.DEBUG) {
            var visibleCount = 0
            engineInstance?.let { engine ->
                GLOBE_COUNTRIES.forEach { if (engine.project(it.xyz) != null) visibleCount++ }
            }
            Box(modifier = Modifier.padding(top = 100.dp, start = 16.dp)) {
                Column {
                    Text(
                        "Markers: $visibleCount / ${GLOBE_COUNTRIES.size}\nRot: ${camera.rotationY.toInt()}, ${camera.rotationX.toInt()}",
                        color = Color.Green,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    engineInstance?.let { engine ->
                        // Selected Country Details
                        selectedCountry?.let { country ->
                            val xyz = country.xyz
                            engine.project(xyz)?.let { res ->
                                DebugCountryTag(country, xyz, res)
                            }
                        }

                        // Reference Points
                        // Greenwich (0, 0)
                        val gPos = GlobeMath.latLonToXyz(0f, 0f)
                        engine.project(gPos)?.let { res ->
                            DebugPin(res.offset, "G", Color.Yellow)
                        }
                        
                        // 90E
                        val ePos = GlobeMath.latLonToXyz(0f, 90f)
                        engine.project(ePos)?.let { res ->
                            DebugPin(res.offset, "90E", Color.Green)
                        }
                        
                        // 90W
                        val wPos = GlobeMath.latLonToXyz(0f, -90f)
                        engine.project(wPos)?.let { res ->
                            DebugPin(res.offset, "90W", Color.Red)
                        }

                        // North Pole
                        val nPos = GlobeMath.latLonToXyz(90f, 0f)
                        engine.project(nPos)?.let { res ->
                            DebugPin(res.offset, "N", Color.Cyan)
                        }

                        // South Pole
                        val sPos = GlobeMath.latLonToXyz(-90f, 0f)
                        engine.project(sPos)?.let { res ->
                            DebugPin(res.offset, "S", Color.Magenta)
                        }
                    }
                }
            }
        }

        if (!hasSeenOnboarding) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable { scope.launch { GameProgressManager.repository.markOnboardingComplete() } },
                contentAlignment = Alignment.Center
            ) {
                Surface(shape = RoundedCornerShape(24.dp), color = PremiumColors.DeepNavy, border = BorderStroke(1.dp, PremiumColors.Gold), modifier = Modifier.padding(32.dp).widthIn(max = 320.dp)) {
                    Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\ud83c\udf0d", fontSize = 56.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("WELCOME TRAVELER!", color = PremiumColors.Gold, fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Drag the globe to explore our world. Tap a country pin to start your culinary journey!", color = Color.White, textAlign = TextAlign.Center, fontSize = 14.sp, lineHeight = 20.sp)
                        Spacer(Modifier.height(28.dp))
                        Button(onClick = { scope.launch { GameProgressManager.repository.markOnboardingComplete() } }, colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(48.dp)) {
                            Text("EXPLORE NOW", fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged { bottomNavHeightPx = it.height.toFloat() }
        ) {
            BottomNavigationBar(currentTab = "world", onTabSelected = onTabSelected)
        }

        // P10-AP: Country Search Overlay
        AnimatedVisibility(
            visible = showSearch,
            enter = fadeIn() + slideInVertically { -it / 8 },
            exit = fadeOut() + slideOutVertically { -it / 8 }
        ) {
            CountrySearchOverlay(
                onCountrySelected = { countryId ->
                    showSearch = false
                    GLOBE_COUNTRIES_BY_ID[countryId]?.let { country ->
                        selectedCountry = country
                        currentCountryId = country.id
                        camera.startFlyTo(country.latDeg, country.lonDeg)
                        GlobalSystemManager.audio.playSfx(SfxType.COUNTRY_CARD_OPEN)
                    }
                },
                onDismiss = { showSearch = false }
            )
        }
        
        // P10-X: Onboarding UI Layers
        if (onboardingStep == OnboardingState.WELCOME_SHOWN) {
            OnboardingTooltip(
                text = "Welcome Traveler!\n\nYour culinary journey spans 213 countries and 3,195 adventures.",
                modifier = Modifier.align(Alignment.Center),
                onDismiss = {
                    scope.launch {
                        GameProgressManager.repository.updateOnboardingState(OnboardingState.WORLD_MAP_INTRO)
                    }
                }
            )
        } else if (onboardingStep == OnboardingState.WORLD_MAP_INTRO) {
             OnboardingTooltip(
                text = "This is your world. Drag to explore, pinch to zoom.\n\nLet's start our first destination in Germany!",
                modifier = Modifier.align(Alignment.Center),
                onDismiss = {
                    scope.launch {
                        GameProgressManager.repository.updateOnboardingState(OnboardingState.FIRST_COUNTRY_SELECTED)
                    }
                    // Auto-focus on Germany
                    val germany = GLOBE_COUNTRIES_BY_ID["germany"]
                    if (germany != null) {
                        camera.startFlyTo(germany.latDeg, germany.lonDeg)
                        currentCountryId = germany.id
                        selectedCountry = germany
                    }
                }
            )
        }
    }
}

@Composable
private fun CountryUnlockBanner(
    countryId: String,
    modifier: Modifier = Modifier
) {
    val metadata = remember(countryId) { LevelRegistry.getCountry(countryId)?.metadata }
    val foods = remember(countryId) { LevelRegistry.getRepresentativeFoods(countryId) }
    
    val pulseTransition = rememberInfiniteTransition(label = "bannerPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 480.dp)
            .scale(pulseScale)
            .shadow(16.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xF2081426),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Brush.linearGradient(listOf(PremiumColors.Gold, Color.Transparent)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\u2605 NEW DESTINATION UNLOCKED",
                color = PremiumColors.Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(metadata?.flagEmoji ?: "\ud83c\udf0d", fontSize = 30.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    metadata?.displayName?.uppercase() ?: countryId.uppercase(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            if (foods.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    foods.take(3).forEach { type ->
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            FoodIcon(type = type, size = 26.dp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Your culinary journey continues...",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BoxScope.GlobeCountryCard(
    country: GlobeCountry,
    onPlayLevel: (Int) -> Unit,
    onShowLevels: () -> Unit,
    onDismiss: () -> Unit,
    bottomClearance: Dp
) {
    val progress  = ProgressionManager.getCountryProgress(country.id)
    val spec      = remember(country.id) { CountryProgressionChain.getSpec(country.id) }
    val metadata  = remember(country.id) { LevelRegistry.getCountry(country.id)?.metadata }
    val foodIcons = remember(country.id) { LevelRegistry.getRepresentativeFoods(country.id) }
    val accentStart = COUNTRY_ACCENT_GRADIENT[country.id]?.first ?: Color(0xFF132A54)
    val accentEnd   = COUNTRY_ACCENT_GRADIENT[country.id]?.second ?: Color(0xFF0A1830)

    val totalLevels       = spec?.totalLevels ?: maxOf(progress.levels.size, 1)
    val completedLevels   = progress.levels.count { it.isCompleted }
    val maxStars          = totalLevels * 3
    val starsNeeded       = spec?.requiredStarsToUnlock ?: 0
    val currentTotalStars = ProgressionManager.playerProgress.totalStars
    val starsRemaining    = maxOf(0, starsNeeded - currentTotalStars)

    val lockProgressFraction by animateFloatAsState(
        targetValue = if (!progress.isUnlocked && starsNeeded > 0) (currentTotalStars.toFloat() / starsNeeded.toFloat()).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "lockProgress"
    )
    val starProgressFraction by animateFloatAsState(
        targetValue = if (progress.isUnlocked && maxStars > 0) (progress.totalStars.toFloat() / maxStars.toFloat()).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "starProgress"
    )

    val nextLevel = if (progress.isUnlocked) {
        progress.levels.indexOfFirst { !it.isCompleted }.let { if (it >= 0) it + 1 else 1 }
    } else 1

    BoxWithConstraints(
        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = bottomClearance),
        contentAlignment = Alignment.Center
    ) {
        // P10-AJ: Breakpoints
        // Compact < 360dp, Small 360-390dp, Standard 390-430dp, Large > 430dp
        val isCompact = maxWidth < 360.dp
        val isSmall = maxWidth >= 360.dp && maxWidth < 390.dp
        
        val cardShape = RoundedCornerShape(if (isCompact) 18.dp else 24.dp)
        val contentHorizontal = if (isCompact) 14.dp else 20.dp
        val contentVertical = if (isCompact) 10.dp else 16.dp
        val maxCardHeight = (maxHeight - bottomClearance - 12.dp).coerceAtLeast(260.dp)

        Surface(
            modifier = Modifier.fillMaxWidth().widthIn(max = 620.dp).heightIn(max = maxCardHeight).shadow(elevation = if (isCompact) 12.dp else 22.dp, shape = cardShape, ambientColor = Color.Black.copy(alpha = 0.32f), spotColor = accentStart.copy(alpha = 0.4f)),
            shape = cardShape,
            color = Color(0xF2080F1E),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentStart.copy(alpha = 0.45f)),
            tonalElevation = 18.dp
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(colors = listOf(accentStart, accentEnd))).padding(horizontal = contentHorizontal, vertical = contentVertical)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = country.flag, fontSize = if (isCompact) 28.sp else 34.sp, modifier = Modifier.padding(end = 12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "EXPLORE DESTINATION", color = Color.White.copy(alpha = 0.55f), fontSize = if (isCompact) 9.sp else 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = country.displayName, color = Color.White, fontSize = if (isCompact) 18.sp else 23.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CountryStatusChip(text = if (progress.isUnlocked) "Available" else "Locked", color = if (progress.isUnlocked) Color(0xFF4AADCC) else Color(0xFF6688AA))
                            Spacer(Modifier.width(8.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = Color.White.copy(alpha = 0.12f)) {
                                Text(
                                    text = metadata?.continent?.displayName?.uppercase() ?: "UNKNOWN",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    TextButton(onClick = onDismiss, contentPadding = PaddingValues(4.dp)) { Text("\u2715", color = Color(0xFF556677), fontSize = 18.sp) }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x1AFFFFFF)))
            Column(modifier = Modifier.padding(horizontal = contentHorizontal, vertical = contentVertical)) {
                val desc = metadata?.travelDescription?.takeIf { it.isNotBlank() }
                if (desc != null) {
                    Text(text = desc, color = Color(0xFF8EA8C0), fontSize = if (isCompact) 13.sp else 14.sp, lineHeight = if (isCompact) 17.sp else 20.sp, modifier = Modifier.padding(bottom = 14.dp))
                } else { Spacer(modifier = Modifier.height(4.dp)) }

                if (foodIcons.isNotEmpty()) {
                    Text(text = "SIGNATURE DISHES", color = Color(0xFF5A7090), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        foodIcons.take(3).forEach { foodType ->
                            Box(modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp)).border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(10.dp)).padding(6.dp), contentAlignment = Alignment.Center) {
                                FoodIcon(type = foodType, size = if (isCompact) 26.dp else 32.dp)
                            }
                        }
                    }
                }

                if (progress.isUnlocked) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 12.dp)) {
                        CountryMetricCard(label = "Stars", value = "${progress.totalStars} / $maxStars", accent = Color(0xFFFFD700), modifier = Modifier.weight(1f), compact = isCompact)
                        CountryMetricCard(label = "Levels", value = "$completedLevels / $totalLevels", accent = Color(0xFF55D494), modifier = Modifier.weight(1f), compact = isCompact)
                        CountryMetricCard(label = "Foods", value = "${progress.discoveredFoods.size} / ${foodIcons.size}", accent = Color(0xFF4AADCC), modifier = Modifier.weight(1f), compact = isCompact)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.White.copy(alpha = 0.10f))) {
                        Box(modifier = Modifier.fillMaxWidth(starProgressFraction).fillMaxHeight().background(Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA200))), RoundedCornerShape(2.dp)))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val playButtonInteraction = remember { MutableInteractionSource() }
                    val isPlayPressed by playButtonInteraction.collectIsPressedAsState()
                    val playScale by animateFloatAsState(targetValue = if (isPlayPressed) 0.975f else 1f, animationSpec = tween(durationMillis = 110), label = "playButtonScale")
                    val glowStrength by animateFloatAsState(targetValue = if (isPlayPressed) 0.66f else 0.96f, animationSpec = tween(durationMillis = 140), label = "playButtonGlow")
                    val playShape = RoundedCornerShape(20.dp)

                    Box(modifier = Modifier.fillMaxWidth().graphicsLayer { scaleX = playScale; scaleY = playScale }.shadow(elevation = if (isPlayPressed) 12.dp else 22.dp, shape = playShape, ambientColor = Color(0xFF2476FF).copy(alpha = 0.58f * glowStrength), spotColor = Color(0xFF00D6FF).copy(alpha = 0.72f * glowStrength)).background(brush = Brush.horizontalGradient(if (progress.isCompleted) listOf(Color(0xFF4CAF50), Color(0xFF81C784)) else listOf(Color(0xFF245BFF), Color(0xFF00D2FF))), shape = playShape).border(width = 1.3.dp, color = Color.White.copy(alpha = 0.55f), shape = playShape)) {
                        Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(9.dp).background(brush = Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = 0.40f), Color.Transparent)), shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)))
                        Button(
                            onClick = { 
                                GlobalSystemManager.audio.playSfx(SfxType.BUTTON_CLICK)
                                onPlayLevel(nextLevel) 
                            }, 
                            interactionSource = playButtonInteraction, 
                            modifier = Modifier.fillMaxWidth().height(if (isCompact) 48.dp else 60.dp), // P10-AJ: 48dp min height
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White), 
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp, focusedElevation = 0.dp, hoveredElevation = 0.dp, disabledElevation = 0.dp), 
                            shape = playShape, 
                            contentPadding = PaddingValues(horizontal = if (isCompact) 8.dp else 16.dp, vertical = 8.dp)
                        ) {
                            val buttonText = when {
                                progress.isCompleted -> "REPLAY ADVENTURE"
                                completedLevels == 0 -> "START ADVENTURE"
                                else -> "CONTINUE ADVENTURE"
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Text(
                                    text = if (progress.isCompleted) "↺" else "▶", 
                                    color = Color.White, 
                                    fontSize = if (isCompact) 16.sp else 22.sp, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    modifier = Modifier.padding(end = if (isCompact) 6.dp else 8.dp)
                                )
                                Text(
                                    text = buttonText, 
                                    color = Color.White, 
                                    fontSize = if (isCompact) 14.sp else 18.sp, // P10-AJ: 14sp for small
                                    fontWeight = FontWeight.Black, 
                                    letterSpacing = if (isCompact) 0.5.sp else 1.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onShowLevels, modifier = Modifier.fillMaxWidth()) { Text(text = "View All Levels", color = Color(0xFF5BA8D0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                } else {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF101824), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("\ud83d\udd12", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text("Destination locked", color = Color(0xFF6688AA), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                if (starsRemaining > 0) { Text("\u2605 $starsRemaining more stars to unlock", color = Color(0xFF4A7FAA), fontSize = 12.sp) }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color.White.copy(alpha = 0.08f))) {
                        Box(modifier = Modifier.fillMaxWidth(lockProgressFraction).fillMaxHeight().background(Brush.horizontalGradient(listOf(Color(0xFF4A9FCC), Color(0xFF7ABDE0))), RoundedCornerShape(2.dp)))
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("\u2605 $currentTotalStars collected", color = Color(0xFF4A9FCC), fontSize = 11.sp)
                        Text("/ $starsNeeded needed", color = Color(0xFF445566), fontSize = 11.sp)
                    }
                    Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF1A2030)), shape = RoundedCornerShape(16.dp)) { Text("\ud83d\udd12  Locked", color = Color(0xFF3A4C64), fontSize = 15.sp) }
                }
                }
            }
        }
    }
}

@Composable
private fun WorldJourneyHUD(
    progressMap: Map<String, com.mahmodhota.worldfood3dadventure.game.progress.CountryProgress>,
    onSearchClick: () -> Unit
) {
    val totalCountries = LevelRegistry.allCountryIds.size
    val completedCountries = progressMap.values.count { it.isCompleted }
    val totalLevels = totalCountries * 15
    val completedLevels = progressMap.values.sumOf { it.levels.count { l -> l.isCompleted } }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(max = 72.dp) // P10-AJ: Max height 72dp
    ) {
        val width = maxWidth
        // P10-AJ: Responsive breakpoints & font sizes
        val (titleSize, valueSize, labelSize) = when {
            width < 360.dp -> Triple(15.sp, 16.sp, 9.sp)
            width < 430.dp -> Triple(17.sp, 18.sp, 10.sp)
            else -> Triple(18.sp, 20.sp, 11.sp)
        }
        
        // Horizontal bar integrated with the top bar - subtle transparency
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "WORLD JOURNEY",
                        color = PremiumColors.Gold.copy(alpha = 0.9f),
                        fontSize = titleSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    // P10-AP: Search Button inside HUD
                    Surface(
                        onClick = onSearchClick,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.size(if (width < 360.dp) 32.dp else 36.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("\ud83d\udd0d", fontSize = if (width < 360.dp) 12.sp else 14.sp)
                        }
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$completedCountries/$totalCountries",
                            color = Color.White,
                            fontSize = valueSize,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "COUNTRIES",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = labelSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$completedLevels/$totalLevels",
                            color = Color.White,
                            fontSize = valueSize,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "LEVELS",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = labelSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Subtle, thin progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
            ) {
                val fraction = (completedLevels.toFloat() / totalLevels.toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(listOf(PremiumColors.Gold, Color(0xFFFFA000)))
                        )
                )
            }
        }
    }
}

@Composable
private fun ContinentSelector(
    selected: com.mahmodhota.worldfood3dadventure.game.world.model.Continent?,
    onContinentSelected: (com.mahmodhota.worldfood3dadventure.game.world.model.Continent) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(4.dp))
        com.mahmodhota.worldfood3dadventure.game.world.model.Continent.entries.forEach { continent ->
            val isSelected = selected == continent
            Surface(
                onClick = { 
                    GlobalSystemManager.audio.playSfx(SfxType.UI_FILTER_SELECT)
                    onContinentSelected(continent) 
                },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) PremiumColors.Gold else Color.Transparent,
                border = if (!isSelected) BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)) else null
            ) {
                Text(
                    text = continent.displayName.uppercase(),
                    color = if (isSelected) PremiumColors.DeepNavy else Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
        Spacer(Modifier.width(4.dp))
    }
}

@Composable
private fun CountrySearchOverlay(
    onCountrySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val allCountries = remember { LevelRegistry.allCountries }
    val filteredResults = remember(query) {
        if (query.isBlank()) emptyList()
        else allCountries.filter { 
            it.displayName.contains(query, ignoreCase = true) || 
            it.levelId.contains(query, ignoreCase = true)
        }.take(15) // Limit results for performance
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.82f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search 213 countries...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PremiumColors.Gold) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedIndicatorColor = PremiumColors.Gold,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.2f)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (query.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Start typing a country name to explore...",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            } else if (filteredResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No countries found matching \"$query\"",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    items(filteredResults, key = { it.levelId }) { country ->
                        SearchCountryRow(country, onClick = { onCountrySelected(country.levelId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchCountryRow(
    country: com.mahmodhota.worldfood3dadventure.game.world.model.CountryMetadata,
    onClick: () -> Unit
) {
    val foods = remember(country.levelId) { LevelRegistry.getRepresentativeFoods(country.levelId) }
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(country.flagEmoji, fontSize = 24.sp)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.displayName.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = country.continent.displayName,
                    color = PremiumColors.Gold.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (foods.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    foods.take(2).forEach { food ->
                        FoodIcon(type = food, size = 22.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryStatusChip(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(999.dp), color = color.copy(alpha = 0.12f), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.28f))) {
        Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun DebugPin(pos: Offset, label: String, color: Color) {
    Box(
        modifier = Modifier
            .offset { IntOffset(pos.x.toInt() - 10, pos.y.toInt() - 10) }
            .size(20.dp)
            .background(color.copy(alpha = 0.6f), CircleShape)
            .border(1.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun DebugCountryTag(
    country: GlobeCountry,
    xyz: FloatArray,
    res: com.mahmodhota.worldfood3dadventure.ui.world3d.filament.FilamentGlobeEngine.ProjectionResult
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(res.offset.x.toInt() + 20, res.offset.y.toInt() - 40) }
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Green.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "ID: ${country.id}\nLat: ${country.latDeg}\nLon: ${country.lonDeg}\nXYZ: [${"%.2f".format(xyz[0])}, ${"%.2f".format(xyz[1])}, ${"%.2f".format(xyz[2])}]",
            color = Color.Green,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 10.sp
        )
    }
}

@Composable
private fun CountryMetricCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier, compact: Boolean) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = Color(0xFF101C2E), border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.16f))) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = if (compact) 10.dp else 12.dp)) {
            Text(text = label.uppercase(), color = Color(0xFF5A7090), fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accent, fontSize = if (compact) 17.sp else 19.sp, fontWeight = FontWeight.Bold)
        }
    }
}
