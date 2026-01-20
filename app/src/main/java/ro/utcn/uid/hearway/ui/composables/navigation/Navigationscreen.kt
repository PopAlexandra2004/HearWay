package ro.utcn.uid.hearway.ui.composables.navigation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
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

enum class NavigationState {
    JOURNEY_ACTIVE,      // Initial state - on the bus
    GPS_SIGNAL_LOST,     // Error state - GPS lost in tunnel
    PREPARE_TO_EXIT,     // ~20 seconds before stop
    GET_OFF_NOW,         // Vehicle stopped at destination
    JOURNEY_COMPLETE     // Success screen
}

@Composable
fun NavigationScreen(
    userProfile: UserProfile?,
    onDismiss: () -> Unit
) {
    var currentState by remember { mutableStateOf(NavigationState.JOURNEY_ACTIVE) }
    var progress by remember { mutableFloatStateOf(0f) }
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND

    // Initial haptic and TTS
    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500)
            TtsManager.speak("Navigation started. You are two stops away from your destination. Press volume down to cancel.")
        }
    }

    // Automatic progression through states
    LaunchedEffect(currentState) {
        when (currentState) {
            NavigationState.JOURNEY_ACTIVE -> {
                // Simulate journey progress over 8 seconds
                for (i in 0..100) {
                    progress = i / 100f
                    delay(80) // 80ms * 100 = 8 seconds total

                    // Simulate GPS loss at 60% progress (like entering a tunnel)
                    if (i == 60) {
                        currentState = NavigationState.GPS_SIGNAL_LOST
                        HapticManager.warning()
                        if (isBlind) {
                            TtsManager.speak("GPS Signal Lost. Reverting to schedule-based alert. Prepare for next scheduled stop alert only.")
                        }
                        return@LaunchedEffect
                    }
                }
                // If no GPS error, move to prepare state
                currentState = NavigationState.PREPARE_TO_EXIT
                HapticManager.prepareToExit()
                if (isBlind) {
                    TtsManager.speak("Prepare to exit. Your stop is next.")
                }
            }
            NavigationState.GPS_SIGNAL_LOST -> {
                // Stay in error state for 3 seconds showing warning
                delay(3000)
                // Then proceed with schedule-based alert
                currentState = NavigationState.GET_OFF_NOW
                HapticManager.getOffNow()
                if (isBlind) {
                    TtsManager.speak("Get off now! Exit here. Scheduled stop reached.")
                }
            }
            NavigationState.PREPARE_TO_EXIT -> {
                // Wait 3 seconds in prepare state
                delay(3000)
                // Move to get off now
                currentState = NavigationState.GET_OFF_NOW
                HapticManager.getOffNow()
                if (isBlind) {
                    TtsManager.speak("Get off now! Exit here.")
                }
            }
            NavigationState.GET_OFF_NOW -> {
                // Stay on this screen for 4 seconds
                delay(4000)
                // Move to complete
                currentState = NavigationState.JOURNEY_COMPLETE
                HapticManager.success()
                if (isBlind) {
                    TtsManager.speak("Journey successfully completed.")
                }
            }
            NavigationState.JOURNEY_COMPLETE -> {
                // Just display success, user can go back
            }
        }
    }

    // Volume down to cancel for blind users
    BackHandler {
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    event.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN
                ) {
                    if (isBlind) {
                        TtsManager.speak("Navigation cancelled")
                    }
                    onDismiss()
                    true
                } else {
                    false
                }
            }
    ) {
        when (currentState) {
            NavigationState.JOURNEY_ACTIVE -> JourneyActiveScreen(progress, isBlind)
            NavigationState.GPS_SIGNAL_LOST -> GpsSignalLostScreen(progress, isBlind)
            NavigationState.PREPARE_TO_EXIT -> PrepareToExitScreen(isBlind)
            NavigationState.GET_OFF_NOW -> GetOffNowScreen(isBlind)
            NavigationState.JOURNEY_COMPLETE -> JourneyCompleteScreen(isBlind, onDismiss)
        }
    }
}

@Composable
fun JourneyActiveScreen(progress: Float, isBlind: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ACTIVE JOURNEY",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Destination: Central Station",
            color = Color.LightGray,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "2 stops away",
            color = Color.Yellow,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Progress bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Proximity Progress",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                color = if (progress > 0.8f) Color.Red else Color.Green,
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isBlind) {
            Text(
                text = "Press Vol Down to cancel",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PrepareToExitScreen(isBlind: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF6F00))  // Orange
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "PREPARE TO EXIT",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your stop is next",
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Get ready to exit the vehicle",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GetOffNowScreen(isBlind: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00FF00)),  // Bright Green
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "GET OFF NOW",
                color = Color.Black,
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 60.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "EXIT HERE",
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GpsSignalLostScreen(progress: Float, isBlind: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Warning icon
        Text(
            text = "⚠️",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Warning dialog box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEB3B))  // Yellow warning
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GPS SIGNAL LOST",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reverting to schedule-based alert",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You will receive exit alert based on scheduled arrival time",
                color = Color.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Show frozen progress bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Proximity Progress (Frozen)",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                color = Color.Gray,
            )
            Text(
                text = "Last known: ${(progress * 100).toInt()}%",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Continuing with scheduled alert...",
            color = Color.Yellow,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun JourneyCompleteScreen(isBlind: Boolean, onDismiss: () -> Unit) {
    BackHandler {
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D32))  // Dark Green
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "✓",
            color = Color.White,
            fontSize = 100.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "JOURNEY COMPLETE",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You have safely exited at your destination",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = if (isBlind) "Press back or Vol Down to return home" else "Press back to return home",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}