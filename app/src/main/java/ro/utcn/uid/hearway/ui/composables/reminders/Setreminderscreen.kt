package ro.utcn.uid.hearway.ui.composables.reminders

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

enum class ReminderState {
    SELECT_ROUTE,
    SELECT_TIME,
    REMINDER_SET
}

@Composable
fun SetReminderScreen(
    userProfile: UserProfile?,
    onDismiss: () -> Unit
) {
    val routes = listOf("Bus 24", "Metro Line 1", "Bus 31")
    val timeOptions = listOf("5 min", "10 min", "15 min")

    var screenState by remember { mutableStateOf(ReminderState.SELECT_ROUTE) }
    var selectedRouteIndex by remember { mutableIntStateOf(0) }
    var selectedTimeIndex by remember { mutableIntStateOf(1) } // Default 10 min
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND

    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500)
            TtsManager.speak("Add reminder? Press volume up for yes, volume down for no.")
        }
    }

    LaunchedEffect(screenState) {
        if (screenState == ReminderState.REMINDER_SET) {
            delay(2500)
            onDismiss()
        }
    }

    BackHandler {
        if (screenState == ReminderState.SELECT_TIME) {
            screenState = ReminderState.SELECT_ROUTE
        } else {
            onDismiss()
        }
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
                            when (screenState) {
                                ReminderState.SELECT_ROUTE -> {
                                    selectedRouteIndex = (selectedRouteIndex + 1) % routes.size
                                    HapticManager.distinct()
                                    if (isBlind) {
                                        TtsManager.speak(routes[selectedRouteIndex])
                                    }
                                }
                                ReminderState.SELECT_TIME -> {
                                    selectedTimeIndex = (selectedTimeIndex + 1) % timeOptions.size
                                    HapticManager.distinct()
                                    if (isBlind) {
                                        TtsManager.speak("${timeOptions[selectedTimeIndex]} before departure")
                                    }
                                }
                                else -> {}
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                            when (screenState) {
                                ReminderState.SELECT_ROUTE -> {
                                    screenState = ReminderState.SELECT_TIME
                                    HapticManager.pulse()
                                    if (isBlind) {
                                        TtsManager.speak("Route selected. When to alert? ${timeOptions[selectedTimeIndex]}")
                                    }
                                }
                                ReminderState.SELECT_TIME -> {
                                    screenState = ReminderState.REMINDER_SET
                                    HapticManager.success()
                                    if (isBlind) {
                                        TtsManager.speak("Reminder set for ${routes[selectedRouteIndex]}, ${timeOptions[selectedTimeIndex]} before departure")
                                    }
                                }
                                else -> {}
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
        when (screenState) {
            ReminderState.SELECT_ROUTE -> {
                Text(
                    text = "SET REMINDER",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Select Route",
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                routes.forEachIndexed { index, route ->
                    val isSelected = index == selectedRouteIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFFFF9800) else Color(0xFF1A1A1A),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 3.dp,
                                color = if (isSelected) Color(0xFFFFB74D) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedRouteIndex = index
                                screenState = ReminderState.SELECT_TIME
                                HapticManager.pulse()
                                if (isBlind) {
                                    TtsManager.speak("Route selected. When to alert?")
                                }
                            }
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = route,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isBlind) {
                    Text(
                        text = "Vol Down: cycle, Vol Up: select",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ReminderState.SELECT_TIME -> {
                Text(
                    text = "⏰",
                    fontSize = 100.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "WHEN TO ALERT?",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "For: ${routes[selectedRouteIndex]}",
                    color = Color(0xFFFF9800),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                timeOptions.forEachIndexed { index, time ->
                    val isSelected = index == selectedTimeIndex
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
                            .clickable {
                                selectedTimeIndex = index
                                screenState = ReminderState.REMINDER_SET
                                HapticManager.success()
                                if (isBlind) {
                                    TtsManager.speak("Reminder set")
                                }
                            }
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$time before",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isBlind) {
                    Text(
                        text = "Vol Down: cycle, Vol Up: confirm",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ReminderState.REMINDER_SET -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.Green,
                        fontSize = 120.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "REMINDER SET",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = routes[selectedRouteIndex],
                        color = Color(0xFFFF9800),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${timeOptions[selectedTimeIndex]} before departure",
                        color = Color.LightGray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📳 Bracelet will vibrate before departure",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}