package ro.utcn.uid.hearway.ui.composables.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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

enum class SaveRouteState {
    CONFIRM_SAVE,
    ROUTE_SAVED
}

@Composable
fun SaveRouteScreen(
    userProfile: UserProfile?,
    onDismiss: () -> Unit
) {
    var screenState by remember { mutableStateOf(SaveRouteState.CONFIRM_SAVE) }
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND
    val routeName = "Work Route" // Simulated saved name

    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500)
            TtsManager.speak("Trip complete. Save this route? Press volume up for yes, volume down for no.")
        }
    }

    LaunchedEffect(screenState) {
        if (screenState == SaveRouteState.ROUTE_SAVED) {
            delay(2000)
            onDismiss()
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
                if (event.type == KeyEventType.KeyDown && screenState == SaveRouteState.CONFIRM_SAVE) {
                    when (event.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                            screenState = SaveRouteState.ROUTE_SAVED
                            HapticManager.success()
                            if (isBlind) {
                                TtsManager.speak("Route saved as $routeName")
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            if (isBlind) {
                                TtsManager.speak("Route not saved")
                            }
                            onDismiss()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (screenState) {
            SaveRouteState.CONFIRM_SAVE -> {
                Text(
                    text = "⭐",
                    fontSize = 100.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SAVE THIS ROUTE?",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Save as: $routeName",
                    color = Color.Yellow,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // No button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF666666), RoundedCornerShape(12.dp))
                            .clickable {
                                if (isBlind) {
                                    TtsManager.speak("Route not saved")
                                }
                                onDismiss()
                            }
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Yes button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                            .clickable {
                                screenState = SaveRouteState.ROUTE_SAVED
                                HapticManager.success()
                                if (isBlind) {
                                    TtsManager.speak("Route saved as $routeName")
                                }
                            }
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "YES",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (isBlind) {
                    Text(
                        text = "Vol Up: Yes, Vol Down: No",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            SaveRouteState.ROUTE_SAVED -> {
                Text(
                    text = "✓",
                    color = Color.Green,
                    fontSize = 120.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "ROUTE SAVED",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "\"$routeName\"",
                    color = Color.Yellow,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick access from favorites",
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}