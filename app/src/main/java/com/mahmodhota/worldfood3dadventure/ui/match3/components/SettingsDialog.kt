package com.mahmodhota.worldfood3dadventure.ui.match3.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.Dialog
import com.mahmodhota.worldfood3dadventure.data.progress.GameProgressManager
import kotlinx.coroutines.launch

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val gameState by GameProgressManager.repository.state.collectAsState()
    val settings = gameState.settings
    val uriHandler = LocalUriHandler.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PremiumColors.DeepNavy,
            border = androidx.compose.foundation.BorderStroke(1.dp, PremiumColors.Gold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SETTINGS",
                    color = PremiumColors.Gold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )

                SettingsToggle(
                    label = "Music",
                    checked = settings.musicVolume > 0f,
                    onCheckedChange = { isOn ->
                        scope.launch {
                            GameProgressManager.repository.updateSettings(musicVolume = if (isOn) 1f else 0f)
                        }
                    }
                )

                SettingsToggle(
                    label = "Sound Effects",
                    checked = settings.sfxVolume > 0f,
                    onCheckedChange = { isOn ->
                        scope.launch {
                            GameProgressManager.repository.updateSettings(sfxVolume = if (isOn) 1f else 0f)
                        }
                    }
                )

                SettingsToggle(
                    label = "Vibration",
                    checked = settings.vibrationEnabled,
                    onCheckedChange = { isOn ->
                        scope.launch {
                            GameProgressManager.repository.updateSettings(vibrationEnabled = isOn)
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                TextButton(
                    onClick = { uriHandler.openUri("https://wadtaktok-dev.github.io/WorldFood3DAdventure/privacy.html") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Privacy Policy", color = Color(0xFF64B5F6), fontSize = 12.sp)
                }

                TextButton(
                    onClick = { uriHandler.openUri("market://details?id=com.mahmodhota.worldfood3dadventure") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Rate WorldFood 3D", color = PremiumColors.Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PremiumColors.Gold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE", fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "Version 1.0.0",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PremiumColors.Gold,
                checkedTrackColor = PremiumColors.Gold.copy(alpha = 0.5f)
            )
        )
    }
}
