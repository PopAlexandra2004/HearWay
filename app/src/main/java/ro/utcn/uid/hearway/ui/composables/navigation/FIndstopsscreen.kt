package ro.utcn.uid.hearway.ui.composables.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ro.utcn.uid.hearway.common.HapticManager
import ro.utcn.uid.hearway.common.UserProfile
import ro.utcn.uid.hearway.common.UserType
import ro.utcn.uid.hearway.tts.TtsManager

@Composable
fun FindStopsScreen(
    userProfile: UserProfile?,
    onStopSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    val stops = listOf(
        StopInfo("Main Street Stop", "180 m", "Visual Signage • Tactile Paving"),
        StopInfo("Central Park Stop", "250 m", "Audio Announcements"),
        StopInfo("University Stop", "400 m", "Visual Signage • Wheelchair Access")
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    var stopSelected by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND

    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500)
            TtsManager.speak("Nearby accessible stops found. Three stops nearby. Use volume down to cycle stops, volume up to select. Currently: ${stops[selectedIndex].name}, ${stops[selectedIndex].distance}")
        }
    }

    LaunchedEffect(stopSelected) {
        if (stopSelected) {
            delay(1500)
            onStopSelected()
        }
    }

    BackHandler {
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            selectedIndex = (selectedIndex + 1) % stops.size
                            HapticManager.distinct()
                            if (isBlind) {
                                val stop = stops[selectedIndex]
                                TtsManager.speak("${stop.name}, ${stop.distance}, ${stop.features}")
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                            stopSelected = true
                            HapticManager.success()
                            if (isBlind) {
                                TtsManager.speak("Stop selected. Starting navigation.")
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!stopSelected) {
            Text(
                text = "NEARBY STOPS",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Fake GPS indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Current Location: Cluj-Napoca",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stop cards
            stops.forEachIndexed { index, stop ->
                StopCard(
                    stop = stop,
                    isSelected = index == selectedIndex,
                    onClick = {
                        selectedIndex = index
                        stopSelected = true
                        HapticManager.success()
                        if (isBlind) {
                            TtsManager.speak("Stop selected. Starting navigation.")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isBlind) {
                Text(
                    text = "Vol Down: cycle stops, Vol Up: select",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Tap a stop to navigate",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Stop selected confirmation
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.Green,
                    fontSize = 100.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "STOP SELECTED",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Starting navigation to stop...",
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StopCard(
    stop: StopInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFF2196F3) else Color(0xFF1A1A1A),
                RoundedCornerShape(12.dp)
            )
            .border(
                width = 3.dp,
                color = if (isSelected) Color.Blue else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stop.name,
                    color = if (isSelected) Color.White else Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stop.features,
                    color = if (isSelected) Color.White else Color.LightGray,
                    fontSize = 14.sp
                )
            }

            Text(
                text = stop.distance,
                color = if (isSelected) Color.White else Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class StopInfo(
    val name: String,
    val distance: String,
    val features: String
)