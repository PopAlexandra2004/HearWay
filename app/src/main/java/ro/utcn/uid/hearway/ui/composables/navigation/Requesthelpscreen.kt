package ro.utcn.uid.hearway.ui.composables.assistance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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

enum class HelpScreenState {
    MESSAGE_SELECTION,
    MESSAGE_DISPLAY
}

@Composable
fun RequestHelpScreen(
    userProfile: UserProfile?,
    onDismiss: () -> Unit
) {
    val messages = listOf(
        "Where is Platform 3?",
        "Where is the restroom?",
        "I need assistance",
        "How do I get to the exit?",
        "Which bus goes to Central Station?",
        "I am lost, please help"
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    var screenState by remember { mutableStateOf(HelpScreenState.MESSAGE_SELECTION) }
    var selectedMessage by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val isBlind = userProfile?.userType == UserType.BLIND

    LaunchedEffect(Unit) {
        HapticManager.pulse()
        focusRequester.requestFocus()
        if (isBlind) {
            delay(500)
            TtsManager.speak("Request assistance. Quick messages available. Use volume down to cycle messages, volume up to display or send. Currently: ${messages[selectedIndex]}")
        }
    }

    BackHandler {
        if (screenState == HelpScreenState.MESSAGE_DISPLAY) {
            screenState = HelpScreenState.MESSAGE_SELECTION
        } else {
            onDismiss()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
                            if (screenState == HelpScreenState.MESSAGE_SELECTION) {
                                selectedIndex = (selectedIndex + 1) % messages.size
                                HapticManager.distinct()
                                if (isBlind) {
                                    TtsManager.speak(messages[selectedIndex])
                                }
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
                            if (screenState == HelpScreenState.MESSAGE_SELECTION) {
                                selectedMessage = messages[selectedIndex]
                                screenState = HelpScreenState.MESSAGE_DISPLAY
                                HapticManager.success()
                                if (isBlind) {
                                    TtsManager.speak("Message ready. ${selectedMessage}. Message sent to staff.")
                                }
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        when (screenState) {
            HelpScreenState.MESSAGE_SELECTION -> MessageSelectionScreen(
                messages = messages,
                selectedIndex = selectedIndex,
                isBlind = isBlind,
                onMessageSelected = { index ->
                    selectedMessage = messages[index]
                    screenState = HelpScreenState.MESSAGE_DISPLAY
                    HapticManager.success()
                    if (isBlind) {
                        TtsManager.speak("Message ready. ${selectedMessage}. Message sent to staff.")
                    }
                }
            )
            HelpScreenState.MESSAGE_DISPLAY -> MessageDisplayScreen(
                message = selectedMessage,
                isBlind = isBlind,
                onBack = { screenState = HelpScreenState.MESSAGE_SELECTION }
            )
        }
    }
}

@Composable
fun MessageSelectionScreen(
    messages: List<String>,
    selectedIndex: Int,
    isBlind: Boolean,
    onMessageSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "REQUEST ASSISTANCE",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select a quick message",
            color = Color.LightGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(messages) { index, message ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            if (isSelected) Color(0xFF2196F3) else Color(0xFF1A1A1A),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 3.dp,
                            color = if (isSelected) Color.Blue else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onMessageSelected(index) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isBlind) {
            Text(
                text = "Vol Down: cycle, Vol Up: select",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "Tap a message to display it",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MessageDisplayScreen(
    message: String,
    isBlind: Boolean,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onBack() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🔊",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "I AM DEAF",
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = message,
                color = Color.Black,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 56.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Please write your response",
                color = Color(0xFF666666),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "✓ Message Sent to Staff",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}