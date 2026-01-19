package ro.utcn.uid.hearway.ui.composables.dashboard

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ro.utcn.uid.hearway.common.HapticManager
import ro.utcn.uid.hearway.common.UserProfile
import ro.utcn.uid.hearway.common.UserType
import ro.utcn.uid.hearway.tts.TtsManager
import ro.utcn.uid.hearway.ui.theme.MyHearwayTheme

@Composable
fun Dashboard(
    userProfile: UserProfile?,
    onNavigate: () -> Unit,
    onCommunicate: () -> Unit,
    onEmergency: () -> Unit
) {
    val buttons = remember {
        listOf(
            "START NAVIGATION" to onNavigate,
            "COMMUNICATE" to onCommunicate,
            "EMERGENCY SOS" to onEmergency
        )
    }
    var selectedButtonIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }

    // State for long press detection
    var volumeDownPressedTime by remember { mutableStateOf(0L) }
    val LONG_PRESS_THRESHOLD_MS = 2000L // 2 seconds

    val isBlind = userProfile?.userType == UserType.BLIND

    LaunchedEffect(isBlind) {
        focusRequester.requestFocus()
        if (isBlind) {
            TtsManager.speak("You are on the dashboard. Use volume up and down to navigate. Long press volume down for emergency. Currently selected: ${buttons[selectedButtonIndex].first}.")
        }
    }

    // Long press detection logic for Volume Down
    LaunchedEffect(volumeDownPressedTime) {
        if (volumeDownPressedTime > 0) {
            delay(LONG_PRESS_THRESHOLD_MS)
            if (System.currentTimeMillis() - volumeDownPressedTime >= LONG_PRESS_THRESHOLD_MS) {
                Log.d("Dashboard", "Volume Down Long Press detected. Activating Emergency.")
                onEmergency()
                volumeDownPressedTime = 0L // Reset after activation
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(all = 20.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event: androidx.compose.ui.input.key.KeyEvent ->
                if (isBlind && event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            // Start long press timer
                            if (volumeDownPressedTime == 0L) {
                                volumeDownPressedTime = System.currentTimeMillis()
                                Log.d("Dashboard", "Volume Down KeyDown. Starting timer and cycling.")
                                
                                // Cycle selection on the first press event
                                selectedButtonIndex = (selectedButtonIndex + 1) % buttons.size
                                HapticManager.distinct()
                                TtsManager.speak(buttons[selectedButtonIndex].first)
                            }
                            return@onKeyEvent true
                        }
                        KeyEvent.KEYCODE_VOLUME_UP -> {
                            Log.d("Dashboard", "Volume Up KeyDown. Activating selected: ${buttons[selectedButtonIndex].first}")
                            HapticManager.pulse()
                            buttons[selectedButtonIndex].second.invoke()
                            volumeDownPressedTime = 0L
                            return@onKeyEvent true
                        }
                    }
                } else if (isBlind && event.type == KeyEventType.KeyUp && event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    volumeDownPressedTime = 0L
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Where to?",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, top = 20.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            buttons.forEachIndexed { index, (label, action) ->
                val isSelected = isBlind && index == selectedButtonIndex
                Button(
                    onClick = {
                        action.invoke()
                        HapticManager.pulse()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (label) {
                            "START NAVIGATION" -> if (isSelected) Color(0xFF66BB6A) else Color(0xFF4CAF50)
                            "COMMUNICATE" -> if (isSelected) Color.LightGray else Color.White
                            "EMERGENCY SOS" -> if (isSelected) Color(0xFFE57373) else Color(0xFFF44336)
                            else -> Color.Gray
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .semantics { contentDescription = label }
                ) {
                    Text(
                        text = label,
                        color = if (label == "COMMUNICATE") Color.Black else Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    MyHearwayTheme {
        Dashboard(
            userProfile = UserProfile(name = "PreviewUser", userType = UserType.DEAF),
            onNavigate = {},
            onCommunicate = {},
            onEmergency = {}
        )
    }
}
