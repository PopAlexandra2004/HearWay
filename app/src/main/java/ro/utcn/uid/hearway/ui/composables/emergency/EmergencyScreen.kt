package ro.utcn.uid.hearway.ui.composables.emergency

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
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

enum class EmergencyState {
    SELECTING, SUCCESS
}

@Composable
fun EmergencyScreen(userProfile: UserProfile?, onDismiss: (showCancelledToast: Boolean) -> Unit) {
    val emergencyTypes = listOf("MEDICAL", "SECURITY", "FIRE", "HARASSMENT", "OTHER")
    var selectedIndex by remember { mutableIntStateOf(0) }
    var currentState by remember { mutableStateOf(EmergencyState.SELECTING) }
    var timeLeft by remember { mutableIntStateOf(10) }
    
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND

    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500) 
            TtsManager.speak("Emergency menu. Use volume down to cycle types, " +
                    "volume up to confirm. Currently selected: ${emergencyTypes[selectedIndex]}")
        }
    }

    LaunchedEffect(currentState, timeLeft) {
        if (currentState == EmergencyState.SELECTING) {
            if (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            } else {
                Log.d("EmergencyScreen", "Timeout reached. Cancelling report.")
                if (isBlind) {
                    TtsManager.speak("Emergency cancelled due to timeout")
                }
                onDismiss(true)
            }
        }
    }

    BackHandler {
        onDismiss(false)
    }

    val handleSelection = {
        val type = emergencyTypes[selectedIndex]
        val message = getEmergencyMessage(type, userProfile?.name)
        sendEmergencyReport(type, message)
        currentState = EmergencyState.SUCCESS
        HapticManager.success()
        if (isBlind) {
            TtsManager.speak(message)
        }
    }

    when (currentState) {
        EmergencyState.SELECTING -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .focusable()
                    .onKeyEvent { event: androidx.compose.ui.input.key.KeyEvent ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.nativeKeyEvent.keyCode) {
                                android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                                    selectedIndex = (selectedIndex + 1) % emergencyTypes.size
                                    HapticManager.distinct()
                                    if (isBlind) {
                                        TtsManager.speak(emergencyTypes[selectedIndex])
                                    }
                                    timeLeft = 10 
                                    true
                                }
                                android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                                    handleSelection()
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
                Text(
                    text = "CONFIRM EMERGENCY TYPE?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(emergencyTypes) { index, type ->
                        val isSelected = index == selectedIndex
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(if (isSelected) Color.White else Color(0xFF1A1A1A))
                                .border(4.dp, if (isSelected) Color.Red else Color.Transparent)
                                .clickable {
                                    selectedIndex = index
                                    handleSelection()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Text(
                    text = if (isBlind) "Vol Down: cycle, Vol Up: confirm" else "Use Vol Down to navigate, Vol Up to confirm.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Auto-cancelling in $timeLeft seconds...",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        EmergencyState.SUCCESS -> {
            val type = emergencyTypes[selectedIndex]
            val message = getEmergencyMessage(type, userProfile?.name)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2E7D32)) 
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "EMERGENCY REPORTED",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable { onDismiss(false) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RETURN HOME",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

private fun getEmergencyMessage(type: String, userName: String?): String {
    val name = userName ?: "the user"
    return when (type) {
        "MEDICAL" -> "Medical emergency reported to 112. Live location and $name's medical " +
                "profile (including name and ID) have been shared with emergency services."
        "SECURITY" -> "Security threat reported. Live location and $name's profile have been " +
                "sent to local security authorities."
        "FIRE" -> "Fire emergency reported to the fire department. Your current location has " +
                "been shared for immediate intervention."
        "HARASSMENT" -> "Harassment reported. Location and $name's details have been sent " +
                "to security and recorded for evidence."
        "OTHER" -> "Emergency alert sent to $name's primary emergency contact. " +
                "Your live location is being shared with them now."
        else -> "Emergency reported. Help is on the way."
    }
}

private fun sendEmergencyReport(type: String, message: String) {
    Log.d("EmergencySystem", "EMERGENCY: $type. Message: $message. Sent at Lat: 46.7712, Lon: 23.5916 (Mock Location)")
}
